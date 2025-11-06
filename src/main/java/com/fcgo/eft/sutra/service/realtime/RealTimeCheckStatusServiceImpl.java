package com.fcgo.eft.sutra.service.realtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.service.RealTimeCheckStatusService;
import com.fcgo.eft.sutra.service.ReconciledTransactionService;
import com.fcgo.eft.sutra.service.realtime.response.ByDatePostCipsByDateResponseWrapper;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransaction;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransactionDetail;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = RuntimeException.class)
public class RealTimeCheckStatusServiceImpl implements RealTimeCheckStatusService {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final WebClient webClient;
    private final ReconciledTransactionService reconciledTransactionService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final StringToJsonNode jsonNode;

    @Override
    public void checkStatusByDate(String date) {
        String apiUrl = url + "/api/getcipstxnlistbydate";
        String accessToken = oauthToken.getAccessToken();
        String payload = "{\"txnDateFrom\":\"" + date + "\",\"txnDateTo\":\"" + date + "\"}";

        Objects.requireNonNull(webClient.post()
                        .uri(apiUrl)
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json")
                        .bodyValue(payload)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<ByDatePostCipsByDateResponseWrapper>>() {
                        })
                        .block())
                .forEach(response -> {
                    RealTimeTransaction batch = response.getCipsBatchDetail();
                    String debitResponseCode = batch.getDebitStatus();
                    long time = new Date().getTime();
                    if (debitResponseCode != null && debitResponseCode.length() > 1) {
                        response.getCipsTransactionDetailList()
                                .forEach(detail -> {
                                    try {
                                        reconciledTransactionService.save(batch, detail, time);
                                    } catch (Exception ex) {
                                        log.info(ex.getMessage());
                                    }
                                });
                    }
                });


    }

    @Override
    public Object checkStatusByInstructionId(String instructionId) {
        long time = new Date().getTime();
        String res = null;

        try {
            res = getRealTimeByBatch(instructionId);
            if (res != null && res.length() > 50) {
                convert(res, time);
            } else {
                log.info(res);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return res;
    }

    public void convert(String res, long time) {

        JsonNode node = jsonNode.toJsonNode(res);
        if (node != null) {
            String instructionId = node.path("batchId").asText();
            long id = node.path("id").asLong();
            String recDate = node.path("recDate").asText();
            String batchCrncy = node.path("batchCrncy").asText();
            String categoryPurpose = node.path("categoryPurpose").asText();
            String debtorAgent = node.path("debtorAgent").asText();
            String debtorBranch = node.path("debtorBranch").asText();
            String debtorName = node.path("debtorName").asText();
            String debtorAccount = node.path("debtorAccount").asText();
            String debitStatus = node.path("debitStatus").asText();
            String settlementDate = node.path("settlementDate").asText();
            String debitReasonDesc = node.path("debitReasonDesc").asText();
            RealTimeTransaction batch = RealTimeTransaction.builder()
                    .id(id)
                    .batchId(instructionId)
                    .recDate(recDate)
                    .batchCrncy(batchCrncy)
                    .categoryPurpose(categoryPurpose)
                    .debtorAgent(debtorAgent)
                    .debtorBranch(debtorBranch)
                    .debtorName(debtorName)
                    .debtorAccount(debtorAccount)
                    .debitStatus(debitStatus)
                    .debitReasonDesc(debitReasonDesc)
                    .settlementDate(settlementDate)
                    .txnResponse(debitReasonDesc)
                    .build();
            jsonNode.toJsonNode(node.get("cipsTransactionDetailList").toString())
                    .forEach(d -> {
                        long did = d.path("id").asLong();

                        Date dRecDate = null;
                        try {
                            dRecDate = dateFormat.parse(d.path("recDate").asText());
                        } catch (Exception ignored) {
                        }
                        String endToEndId = d.path("endToEndId").asText();
                        double amount = d.path("amount").asDouble();
                        double chargeAmount = d.path("chargeAmount").asDouble();
                        String chargeLiability = d.path("chargeLiability").asText();
                        String purpose = d.path("purpose").asText();
                        String creditorAgent = d.path("creditorAgent").asText();
                        String creditorBranch = d.path("creditorBranch").asText();
                        String creditorName = d.path("creditorName").asText();
                        String creditorAccount = d.path("creditorAccount").asText();
                        long addenda1 = d.path("addenda1").asLong();
                        String addenda2 = d.path("addenda2").asText();
                        String addenda3 = d.path("addenda3").asText();
                        String addenda4 = d.path("addenda4").asText();
                        String creditStatus = d.path("creditStatus").asText();
                        String reasonDesc = d.path("reasonDesc").asText();
                        String refId = d.path("refId").asText();
                        String particulars = d.path("particulars").asText();
                        RealTimeTransactionDetail detail = RealTimeTransactionDetail.builder()
                                .id(did)
                                .recDate(dRecDate)
                                .instructionId(Long.parseLong(instructionId))
                                .endToEndId(endToEndId)
                                .chargeLiability(chargeLiability)
                                .purpose(purpose)
                                .creditStatus(creditStatus)
                                .reasonCode(reasonDesc)
                                .remarks(reasonDesc)
                                .particulars(particulars)
                                .reasonDesc(reasonDesc)
                                .amount(amount)
                                .chargeAmount(chargeAmount)
                                .creditorAgent(creditorAgent)
                                .creditorBranch(creditorBranch)
                                .creditorName(creditorName)
                                .creditorAccount(creditorAccount)
                                .addenda1(addenda1)
                                .addenda2(addenda2)
                                .addenda3(addenda3)
                                .addenda4(addenda4)
                                .refId(refId)
                                .build();
                        reconciledTransactionService.save(batch, detail, time);
                    });

        }

    }

    public String getRealTimeByBatch(String instructionId) {
        try {
            return Objects.requireNonNull(webClient.post()
                    .uri(url + "/api/getcipstxnlistbybatchid")
                    .header("Authorization", "Bearer " + oauthToken.getAccessToken())
                    .header("Content-Type", "application/json")
                    .bodyValue("{\"batchId\":\"" + instructionId + "\"}")
                    .retrieve().bodyToMono(String.class)
                    .block());
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
