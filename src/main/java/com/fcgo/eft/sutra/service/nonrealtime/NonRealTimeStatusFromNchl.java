package com.fcgo.eft.sutra.service.nonrealtime;


import com.fcgo.eft.sutra.dto.nchlres.NonRealTimeBatch;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class NonRealTimeStatusFromNchl {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final WebClient webClient;


    public NonRealTimeBatch checkByBatchNonRealTime(String batchId) {
        String apiUrl = url + "/api/getnchlipstxnlistbybatchid";
        String accessToken = oauthToken.getAccessToken();
        String payload = "{\"batchId\":\"" + batchId + "\"}";

        return webClient.post()
                .uri(apiUrl).header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(NonRealTimeBatch.class)
                .block();

    }
}
