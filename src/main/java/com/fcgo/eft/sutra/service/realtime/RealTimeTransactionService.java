package com.fcgo.eft.sutra.service.realtime;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcgo.eft.sutra.dto.res.EftPaymentRequestDetailProjection;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.service.impl.NchlReconciledService;
import com.fcgo.eft.sutra.service.realtime.response.CipsBatchResponse;
import com.fcgo.eft.sutra.service.realtime.response.CipsTxnResponse;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeResponse;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import com.fcgo.eft.sutra.token.TokenGenerate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.DecimalFormat;
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
    private final DecimalFormat df = new DecimalFormat("#.00");
    private final ObjectMapper mapper = new ObjectMapper();

    @Setter
    @Getter
    private int count = 0;
    public void ipsDctTransaction(EftPaymentRequestDetailProjection m, String creditorBranch) {

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
            repository.updateNchlStatusByInstructionId("SENT", instructionId);
            assert response != null;
            CipsBatchResponse nchl = response.getCipsBatchResponse();
            if (!response.getCipsTxnResponseList().isEmpty()) {

                CipsTxnResponse txn = response.getCipsTxnResponseList().get(0);
                if (nchl.getDebitStatus().equals("000") && txn.getCreditStatus().equals("000")) {
                    long eftNo = Long.parseLong(instructionId);
                    NchlReconciled reconciled = reconciledRepository.save(eftNo, "000", nchl.getResponseMessage(), txn.getCreditStatus(), txn.getResponseMessage(), String.valueOf(txn.getId()), new Date());
                    epaymentRepository.updateEPaymentLog(reconciled.getCreditMessage(), eftNo);
                    epaymentRepository.updateSuccessEPayment(reconciled.getCreditMessage(), reconciled.getRecDate(), eftNo);
                    reconciledRepository.updateStatus(instructionId);
                }
            }
            log.info("REAL TIME TRANSACTION PUSHED IN  NCHL  {}", instructionId);
        } catch (Exception e) {
            repository.updateNchlStatusByInstructionId("SENT", instructionId);

            try {
                long eftNo = Long.parseLong(instructionId);
                JsonNode jsonNode = mapper.readTree(e.getMessage());
                String responseCode = jsonNode.get("responseCode").asText();
                String responseDescription = jsonNode.get("responseDescription").asText();
                if (responseCode.equals("E007")) {
                    failure("Please Conform with bank: " + responseDescription, instructionId, eftNo);
                }
            } catch (Exception ignored) {
            }
        }
        count--;
    }

    private void failure(String responseDescription, String instructionId, long eftNo) {
        if (responseDescription.length() > 500) responseDescription = responseDescription.substring(0, 490);
        epaymentRepository.updateEPaymentLog(responseDescription, eftNo);
        epaymentRepository.updateFailureEPayment(responseDescription, eftNo);
        reconciledRepository.updateStatus(instructionId);
    }

}
