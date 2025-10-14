package com.fcgo.eft.sutra.service.realtime;

import com.fcgo.eft.sutra.service.ReconciledTransactionService;
import com.fcgo.eft.sutra.service.impl.NchlReconciledService;
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
public class RealTimeStatusFromNchl {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final WebClient webClient;
    private final NchlReconciledService repository;
    private final ReconciledTransactionService reconciledTransactionService;

    public void realTimeCheckByDate(String date) {
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
                .parallelStream()
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


    public void realTimeCheckByDate(String dateFrom, String dateTo) {
        String apiUrl = url + "/api/getcipstxnlistbydate";
        String accessToken = oauthToken.getAccessToken();
        String payload = "{\"txnDateFrom\":\"" + dateFrom + "\",\"txnDateTo\":\"" + dateTo + "\"}";

        try {
            List<ByDatePostCipsByDateResponseWrapper> res = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ByDatePostCipsByDateResponseWrapper>>() {
                    })
                    .block();

            assert res != null;
            res.forEach(response -> {
                String debitResponseCode = response.getCipsBatchDetail().getDebitStatus();
                String debitResponseMessage = response.getCipsBatchDetail().getDebitReasonDesc();
                if (debitResponseCode != null && debitResponseCode.length() > 1) {
                    try {
                        response.getCipsTransactionDetailList()
                                .forEach(detail -> {
                                    try {
                                        if (detail.getCreditStatus() != null) {
                                            log.info("InstructionId: {} status: {} {}", detail.getInstructionId(), detail.getCreditStatus(), detail.getReasonDesc());
                                            repository.save(detail.getInstructionId(), debitResponseCode, debitResponseMessage, detail.getCreditStatus(), detail.getReasonDesc(), detail.getInstructionId() + "", detail.getRecDate());
                                        }
                                    } catch (Exception e) {
                                    }
                                });
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                }
            });


        } catch (Exception e) {
            log.info(" {}", e.getMessage());
        }
    }

    public String getRealTimeByBatch(String instructionId) {
        String response;
        try {
            response = webClient.post()
                    .uri(url + "/api/getcipstxnlistbybatchid")
                    .header("Authorization", "Bearer " + oauthToken.getAccessToken())
                    .header("Content-Type", "application/json")
                    .bodyValue("{\"batchId\":\"" + instructionId + "\"}")
                    .retrieve().bodyToMono(String.class)
                    .block();
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
