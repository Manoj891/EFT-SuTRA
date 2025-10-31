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
import com.fcgo.eft.sutra.service.realtime.response.CipsBatchResponse;
import com.fcgo.eft.sutra.service.realtime.response.CipsTxnResponse;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeResponse;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import com.fcgo.eft.sutra.token.TokenGenerate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealTimeTransactionService {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final TokenGenerate tokenGenerate;
    private final WebClient webClient;
    private final EftBatchPaymentDetailRepository repository;
    private final NchlReconciledService reconciledRepository;
    private final AccEpaymentRepository epaymentRepository;
    private final RealTimeCheckStatusService realTime;
    private final StringToJsonNode jsonNode;
    private final DecimalFormat df = new DecimalFormat("#.00");
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");


    public void ipsDctTransaction(EftPaymentRequestDetailProjection m, String creditorBranch, Integer tryCount) {

        String amount = df.format(m.getAmount());
        String debtorBranch = m.getDebtorBranch();
        String debtorAgent = m.getDebtorAgent();
        String debtorName = m.getDebtorName();
        String debtorAccount = m.getDebtorAccount();
        String creditorName = m.getCreditorName();
        String creditorAccount = m.getCreditorAccount();
        String creditorAgent = m.getCreditorAgent();

        String token = tokenGenerate.geterateHashCipsBatch(m.getInstructionId(), debtorBranch, debtorAgent, debtorAccount, creditorAccount, creditorAgent, creditorBranch, amount);
        String payload = "{\"cipsBatchDetail\":{\"batchId\":\"" + m.getInstructionId() + "\",\"batchAmount\":\"" + amount + "\",\"batchCount\":\"1\",\"batchCrncy\":\"NPR\",\"categoryPurpose\":\"" + m.getCategoryPurpose() + "\",\"debtorAgent\":\"" + debtorAgent + "\",\"debtorBranch\":\"" + debtorBranch + "\",\"debtorName\":\"" + debtorName + "\",\"debtorAccount\":\"" + debtorAccount + "\"}," +
                "\"cipsTransactionDetailList\":[{\"instructionId\":\"" + m.getInstructionId() + "\",\"endToEndId\":\"" + m.getEndToEndId() + "\",\"amount\":\"" + amount + "\",\"purpose\":\"" + m.getCategoryPurpose() + "\",\"creditorAgent\":\"" + creditorAgent + "\",\"creditorBranch\":\"" + creditorBranch + "\",\"creditorName\":\"" + creditorName + "\",\"creditorAccount\":\"" + creditorAccount + "\",\"addenda1\":\"" + m.getAddenda1() + "\",\"addenda2\":\"" + m.getAddenda2() + "\",\"addenda3\":\"" + m.getAddenda3() + "\",\"addenda4\":\"" + m.getAddenda4() + "\",\"channelId\":\"IPS\",\"refId\":\"" + m.getRefId() + "\",\"remarks\":\"" + m.getRemarks() + "\"}]," +
                "\"token\":\"" + token + "\"}";
        String instructionId = m.getInstructionId();
        String accessToken = oauthToken.getAccessToken();
        String apiUrl = url + "/api/postcipsbatch";
        long dateTime = Long.parseLong(sdf.format(new Date()));
        try {
            RealTimeResponse response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(payload)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                            .map(CustomException::new))
                    .bodyToMono(RealTimeResponse.class).block();


            assert response != null;
            CipsBatchResponse nchl = response.getCipsBatchResponse();
            repository.updateRealTimeTransactionStatus("SENT", dateTime, (tryCount + 1), instructionId);
            if (!response.getCipsTxnResponseList().isEmpty()) {
                CipsTxnResponse txn = response.getCipsTxnResponseList().get(0);
                if (nchl.getDebitStatus().equals("000") && txn.getCreditStatus().equals("000")) {
                    long eftNo = Long.parseLong(instructionId);
                    NchlReconciled reconciled = reconciledRepository.save(eftNo, "000", nchl.getResponseMessage(), txn.getCreditStatus(), txn.getResponseMessage(), String.valueOf(txn.getId()), new Date());
                    String message = reconciled.getCreditMessage();
                    if (message != null && message.length() > 500) message = message.substring(0, 500);
                    epaymentRepository.updateSuccessEPayment(message, reconciled.getRecDate(), eftNo);
                    reconciledRepository.updateStatus(instructionId);
                    log.info("REAL TIME TRANSACTION PUSHED IN  NCHL  {} Success", instructionId);
                } else {
                    log.info("REAL TIME TRANSACTION PUSHED IN  NCHL  {} Not Success", instructionId);
                    realTime.checkStatusByInstructionId(instructionId);
                }
            } else {
                log.info("REAL TIME TRANSACTION PUSHED IN  NCHL  {} Not Success", instructionId);
                realTime.checkStatusByInstructionId(instructionId);
            }

        } catch (Exception e) {
            if (tryCount == null) tryCount = 1;
            JsonNode node = jsonNode.toJsonNode(e.getMessage());
            if (node != null) {
                String code = node.get("responseCode").asText();
                String description = node.get("responseDescription").asText();
                if (getStatus(code)) {
                    if (tryCount > 10) {
                        repository.updateRealTimeTransactionStatus("SENT", dateTime, (tryCount + 1), instructionId);
                        failure(code, description, instructionId);
                    } else {
                        repository.updateNextTryInstructionId((tryCount + 1), dateTime, instructionId);
                        reconciledRepository.save(Long.parseLong(instructionId), "000", "Waiting...", "SENT", description + ". We will try again " + (10 - tryCount) + " Time", instructionId, new Date());
                    }
                }
            }
            log.info("REAL TIME TRANSACTION PUSHED IN  NCHL  {} {}", instructionId, e.getMessage());
        }
    }

    private boolean getStatus(String code) {
        return    (code.equalsIgnoreCase("E001")
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

    private void failure(String code, String description, String instructionId) {
        long eftNo = Long.parseLong(instructionId);
        reconciledRepository.save(eftNo, code, description, "1000", description, instructionId, new Date());
        if (description.length() > 500) description = description.substring(0, 490);
        epaymentRepository.updateFailureEPayment(description, eftNo);
        reconciledRepository.updateStatus(instructionId);
    }

}
