package com.fcgo.eft.sutra.service.realtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.dto.res.EftPaymentRequestDetailProjection;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.service.RealTimeCheckStatusService;
import com.fcgo.eft.sutra.service.impl.NchlReconciledService;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import com.fcgo.eft.sutra.token.TokenGenerate;
import com.fcgo.eft.sutra.util.IsProdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealTimeTransactionServiceImpl implements RealTimeTransactionService {

    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final TokenGenerate tokenGenerate;
    private final WebClient webClient;
    private final EftBatchPaymentDetailRepository repository;
    private final NchlReconciledService reconciledRepository;
    private final AccEpaymentRepository epaymentRepository;
    private final RealTimeCheckStatusService realTime;
    private final IsProdService isProdService;
    private final StringToJsonNode jsonNode;
    private final DecimalFormat df = new DecimalFormat("#.00");
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public void pushPayment(EftPaymentRequestDetailProjection m, String creditorBranch) {
        String amount = df.format(m.getAmount());
        String debtorBranch = m.getDebtorBranch();
        String debtorAgent = m.getDebtorAgent();
        String debtorName = m.getDebtorName();
        String debtorAccount = m.getDebtorAccount();
        String creditorName = m.getCreditorName();
        String creditorAccount = m.getCreditorAccount();
        String creditorAgent = m.getCreditorAgent();
        Integer tryCount = m.getTryCount();
        String token = tokenGenerate.geterateHashCipsBatch(m.getInstructionId(), debtorBranch, debtorAgent, debtorAccount, creditorAccount, creditorAgent, creditorBranch, amount);
        String payload = "{\"cipsBatchDetail\":{\"batchId\":\"" + m.getInstructionId() + "\",\"batchAmount\":\"" + amount + "\",\"batchCount\":\"1\",\"batchCrncy\":\"NPR\",\"categoryPurpose\":\"" + m.getCategoryPurpose() + "\",\"debtorAgent\":\"" + debtorAgent + "\",\"debtorBranch\":\"" + debtorBranch + "\",\"debtorName\":\"" + debtorName + "\",\"debtorAccount\":\"" + debtorAccount + "\"}," +
                "\"cipsTransactionDetailList\":[{\"instructionId\":\"" + m.getInstructionId() + "\",\"endToEndId\":\"" + m.getEndToEndId() + "\",\"amount\":\"" + amount + "\",\"purpose\":\"" + m.getCategoryPurpose() + "\",\"creditorAgent\":\"" + creditorAgent + "\",\"creditorBranch\":\"" + creditorBranch + "\",\"creditorName\":\"" + creditorName + "\",\"creditorAccount\":\"" + creditorAccount + "\",\"addenda1\":\"" + m.getAddenda1() + "\",\"addenda2\":\"" + m.getAddenda2() + "\",\"addenda3\":\"" + isProdService.getProdIpAddress() + "\",\"addenda4\":\"" + m.getAddenda4() + "\",\"channelId\":\"IPS\",\"refId\":\"" + m.getRefId() + "\",\"remarks\":\"" + m.getRemarks() + "\"}]," +
                "\"token\":\"" + token + "\"}";
        String instructionId = m.getInstructionId();
        long eftNo=Long.parseLong(instructionId);
        String accessToken = oauthToken.getAccessToken();
        String apiUrl = url + "/api/postcipsbatch";
        long dateTime = Long.parseLong(sdf.format(new Date()));
        String response = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .publishOn(Schedulers.boundedElastic())
                                .doOnNext(errorBody -> {
                                    try {
                                        JsonNode node = jsonNode.toJsonNode(errorBody);
                                        if (node != null) {
                                            String code = node.get("responseCode").asText();
                                            String message;
                                            if (getStatus(code)) {
                                                JsonNode nMessage = node.get("responseMessage");
                                                JsonNode nDescription = node.get("responseDescription");
                                                if (nMessage != null) {
                                                    message = nMessage.asText();
                                                } else if (nDescription != null) {
                                                    message = nDescription.asText();
                                                } else {
                                                    message = errorBody;
                                                    if (message.length() > 500) message = message.substring(0, 499);
                                                }
                                                log.info("{} {} {} {} ", code, message, instructionId, tryCount);
                                                epaymentRepository.updateMessage(message, eftNo);
                                                getErrorE0N(tryCount, code, message, instructionId, dateTime);
                                            } else {
                                                log.info("{} {} {} {}", code, instructionId, tryCount, errorBody);
                                                epaymentRepository.updateMessage(errorBody, eftNo);
                                                realTime.checkStatusByInstructionId(instructionId);

                                            }
                                        }
                                    } catch (Exception ex) {
                                        log.info("API Error: {} {} {} {}", errorBody, instructionId, tryCount, ex.getMessage());
                                        epaymentRepository.updateMessage(errorBody, eftNo);
                                        realTime.checkStatusByInstructionId(instructionId);
                                    }
                                })
                                .then(Mono.empty())    // do NOT throw exception
                )
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.empty())   // safety: catch any unexpected errors
                .block();

        JsonNode node = response != null ? jsonNode.toJsonNode(response) : null;
        if (node != null) {
            try {
                JsonNode n = node.get("cipsBatchResponse");
                String code = n.get("responseCode").asText();
                String message = n.get("responseMessage").asText();
                if (code.equalsIgnoreCase("000")) {
                    int finalTryCount = tryCount;
                    node.get("cipsTxnResponseList").forEach(nd -> {
                        String creditStatus = nd.get("creditStatus").asText();
                        if (creditStatus.equalsIgnoreCase("000")) {
                            try {
                                success(finalTryCount, dateTime, instructionId, nd.get("id").asText());
                            } catch (Exception e) {
                                log.info("{} {} {}", code, message, instructionId);
                                epaymentRepository.updateMessage(message, eftNo);
                                realTime.checkStatusByInstructionId(instructionId);
                            }
                        }
                    });
                } else if (getStatus(code)) {
                    epaymentRepository.updateMessage(message, eftNo);
                    getErrorE0N(tryCount, code, message, instructionId, dateTime);
                } else {
                    log.info("{} {} {}", code, message, instructionId);
                    epaymentRepository.updateMessage(message, eftNo);
                    realTime.checkStatusByInstructionId(instructionId);
                }
            } catch (Exception ex) {
                log.info("{} {}", ex.getMessage(), instructionId);
            }
        }
    }


    private boolean getStatus(String code) {
        return (code.equalsIgnoreCase("E001")
                || code.equalsIgnoreCase("E002")
                || code.equalsIgnoreCase("E003")
                || code.equalsIgnoreCase("E004")
                || code.equalsIgnoreCase("E005")
                || code.equalsIgnoreCase("E006")
                || code.equalsIgnoreCase("E007")
                || code.equalsIgnoreCase("E008")
                || code.equalsIgnoreCase("E009")
                || code.equalsIgnoreCase("E010")
                || code.equalsIgnoreCase("E011")
                || code.equalsIgnoreCase("E012"));
    }

    private void getErrorE0N(int tryCount, String code, String description, String instructionId, long dateTime) {
        int count = (tryCount + 1);
        String time = count == 1 ? "1st" :
                count == 2 ? "2nd" :
                        count == 3 ? "3rd" :
                                (count + "th");
        if (tryCount > 15) {
            failure(code, description + ". Transaction reject after tried " + time + " times.", instructionId);
        } else {
            repository.updateNextTryInstructionId(count, dateTime, instructionId);
            reconciledRepository.save(Long.parseLong(instructionId), "000", "Waiting...", "SENT", description + ". We Tried " + time + " times.", instructionId, new Date());
        }
    }

    private void failure(String code, String description, String instructionId) {
        long eftNo = Long.parseLong(instructionId);
        if (description.length() > 500) description = description.substring(0, 490);
        reconciledRepository.save(eftNo, "EFT", "SuTRA", code, description, instructionId, new Date());

        epaymentRepository.updateFailureEPayment(description, eftNo);
        reconciledRepository.updateStatus(instructionId);
    }

    private void success(int finalTryCount, long dateTime, String instructionId, String id) {
        repository.updateRealTimeTransactionStatus("SENT", dateTime, (finalTryCount + 1), instructionId);
        long eftNo = Long.parseLong(instructionId);
        NchlReconciled reconciled = reconciledRepository.save(eftNo, "000", "-", "000", "SUCCESS", id, new Date());
        String message = reconciled.getCreditMessage();
        if (message != null && message.length() > 500) message = message.substring(0, 500);
        epaymentRepository.updateSuccessEPayment(message, reconciled.getRecDate(), eftNo);
        reconciledRepository.updateStatus(instructionId);
        log.info("REAL TIME TRANSACTION PUSHED IN NCHL {} SUCCESS", instructionId);
    }
}
