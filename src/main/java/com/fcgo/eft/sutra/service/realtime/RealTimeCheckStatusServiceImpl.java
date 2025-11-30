package com.fcgo.eft.sutra.service.realtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.service.RealTimeCheckStatusService;
import com.fcgo.eft.sutra.service.ReconciledTransactionService;
import com.fcgo.eft.sutra.service.realtime.response.ByDatePostCipsByDateResponseWrapper;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransaction;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final NchlReconciledRepository reconciledRepository;
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
    public Object checkStatusByInstructionId(String instructionId, int times) {

        String res = null;

        try {
            long pushedDatetime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date()));
            res = getRealTimeByBatch(instructionId);
            if (res != null && res.length() > 50) {
                convert(res);
            } else if (times >= 15) {
                failure(instructionId);
            } else log.info(res);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return res;
    }

    @Override
    public void convert(String res) {

        JsonNode node = jsonNode.toJsonNode(res);
        if (node != null) {
            String id = node.path("id").asText();
            String debitStatus = node.path("debitStatus").asText();
            String debitReasonDesc = node.path("debitReasonDesc").asText();
            node.get("cipsTransactionDetailList").forEach(d -> {
                Date dRecDate = null;
                try {
                    dRecDate = jsonNode.getDateFormat().parse(d.path("recDate").asText());
                } catch (Exception ignored) {
                }
                long instructionId = d.path("instructionId").asLong();
                String creditStatus = d.path("creditStatus").asText();
                String reasonDesc = d.path("reasonDesc").asText();
                reconciledRepository.save(NchlReconciled.builder().instructionId(instructionId).debitStatus(debitStatus).debitMessage(debitReasonDesc).creditStatus(creditStatus).creditMessage(reasonDesc).recDate(dRecDate).transactionId(id).pushed("N").build());
                log.info("InstructionId: {} status: {} {}", instructionId, creditStatus, reasonDesc);
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


    private void failure(String instructionId) {
        reconciledRepository.findById(Long.parseLong(instructionId)).ifPresent(reconciled -> {
            if (reconciled.getCreditStatus().equals("SENT")) {
                reconciledRepository.updateManualReject(instructionId);
                reconciledRepository.updateManualReject(Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date())), instructionId);
                log.info("Manually rejected {}", instructionId);
            }
        });
    }
}
