package com.fcgo.eft.sutra.service.realtime;

import com.fcgo.eft.sutra.service.RealTimeCheckStatusService;
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
public class RealTimeCheckStatusServiceImpl implements RealTimeCheckStatusService {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final WebClient webClient;
    private final NchlReconciledService repository;
    private final ReconciledTransactionService reconciledTransactionService;

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
