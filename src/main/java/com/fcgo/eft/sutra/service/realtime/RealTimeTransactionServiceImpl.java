package com.fcgo.eft.sutra.service.realtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.dto.res.EftPaymentRequestDetailProjection;
import com.fcgo.eft.sutra.repository.EftBatchPaymentDetailRepository;
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
    private final RealTimeCheckStatusService realTime;
    private final IsProdService isProdService;
    private final StringToJsonNode jsonNode;
    private final DecimalFormat df = new DecimalFormat("#.00");


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
        String accessToken = oauthToken.getAccessToken();
        String apiUrl = url + "/api/postcipsbatch";
        long dateTime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date()));
        String response = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .publishOn(Schedulers.boundedElastic())
                                .doOnNext(errorBody -> handelError(errorBody, instructionId, tryCount, dateTime))
                                .then(Mono.empty())    // do NOT throw exception
                )
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.empty())   // safety: catch any unexpected errors
                .block();
        try {
            handelSuccess(response, instructionId, tryCount, dateTime);
        } catch (Exception ignored) {
        }

    }

    private void handelSuccess(String response, String instructionId, int tryCount, long dateTime) {
        JsonNode node = response != null ? jsonNode.toJsonNode(response) : null;
        if (node != null) {
            repository.updateRealTimeTransactionStatus("SENT", dateTime, (tryCount + 1), instructionId);
            try {
                JsonNode n = node.get("cipsBatchResponse");
                String code = n.get("responseCode").asText();
                String message = n.get("responseMessage").asText();
                if (code.equalsIgnoreCase("000")) {
                    node.get("cipsTxnResponseList").forEach(nd -> {
                        String creditStatus = nd.get("creditStatus").asText();
                        if (creditStatus.equalsIgnoreCase("000")) {
                            try {
                                success(instructionId, nd.get("id").asText());
                            } catch (Exception e) {
                                log.info("{} {} {}", code, message, instructionId);
                                realTime.checkStatusByInstructionId(instructionId, tryCount);
                            }
                        }
                    });
                } else if (getStatus(code)) {
                    getErrorE0N(tryCount, code, message, instructionId, dateTime);
                } else {
                    log.info("{} {} {}", code, message, instructionId);
                    realTime.checkStatusByInstructionId(instructionId, tryCount);
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

    private void handelError(String errorBody, String instructionId, int tryCount, long dateTime) {
        repository.updateRealTimeTransactionStatus("SENT", dateTime, (tryCount + 1), instructionId);
        try {
            JsonNode node = jsonNode.toJsonNode(errorBody);
            if (node != null) {
                String code = node.get("responseCode").asText();
                String message;
                if (getStatus(code)) {
                    JsonNode nMessage = null, nDescription = null;
                    try {
                        nMessage = node.get("responseMessage");
                    } catch (Exception ignored) {
                    }
                    try {
                        nDescription = node.get("responseDescription");
                    } catch (Exception ignored) {
                    }
                    if (nMessage != null) {
                        message = nMessage.asText();
                    } else if (nDescription != null) {
                        message = nDescription.asText();
                    } else {
                        message = errorBody;
                        if (message.length() > 500) message = message.substring(0, 499);
                    }
                    log.info("{} {} {} {} ", code, message, instructionId, tryCount);
                    getErrorE0N(tryCount, code, message, instructionId, dateTime);
                } else {
                    log.info("{} {} {} {}", code, instructionId, tryCount, errorBody);
                    realTime.checkStatusByInstructionId(instructionId, tryCount);
                }
            }
        } catch (Exception ex) {
            log.info("API Error: {} {} {} {}", errorBody, instructionId, tryCount, ex.getMessage());
            realTime.checkStatusByInstructionId(instructionId, tryCount);
        }
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
            reconciledRepository.save(Long.parseLong(instructionId), "000", "-", "SENT", description + ". We Tried " + time + " times.", instructionId, new Date());
        }
    }

    private void failure(String code, String description, String instructionId) {
        long eftNo = Long.parseLong(instructionId);
        if (description.length() > 500) description = description.substring(0, 490);
        reconciledRepository.save(eftNo, "EFT", "SuTRA", code, description, instructionId, new Date());
    }

    private void success(String instructionId, String id) {
        long eftNo = Long.parseLong(instructionId);
        reconciledRepository.save(eftNo, "000", "-", "000", "SUCCESS", id, new Date());
        log.info("REAL TIME TRANSACTION PUSHED IN NCHL {} SUCCESS", instructionId);
    }
}
