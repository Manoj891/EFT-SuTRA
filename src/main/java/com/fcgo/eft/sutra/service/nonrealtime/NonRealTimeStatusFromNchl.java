package com.fcgo.eft.sutra.service.nonrealtime;


import com.fcgo.eft.sutra.dto.PostCipsByDateResponseWrapper;
import com.fcgo.eft.sutra.dto.nchlres.NonRealTimeBatch;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class NonRealTimeStatusFromNchl {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final WebClient webClient;

    public List<PostCipsByDateResponseWrapper> checkStatusByDate(String date) {
        String apiUrl = url + "/api/getnchlipstxnlistbydate";
        String accessToken = oauthToken.getAccessToken();
        String payload = "{\"txnDateFrom\":\"" + date + "\",\"txnDateTo\":\"" + date + "\"} ";
        return Objects.requireNonNull(
                webClient.post()
                        .uri(apiUrl)
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json")
                        .bodyValue(payload)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .flatMap(errorBody -> {
                                            log.error(errorBody);
                                            return Mono.empty();
                                        })
                        )
                        .bodyToMono(new ParameterizedTypeReference<List<PostCipsByDateResponseWrapper>>() {
                        }).block());

    }

    public NonRealTimeBatch checkByBatchNonRealTime(String batchId) {
        String apiUrl = url + "/api/getnchlipstxnlistbybatchid";
        String accessToken = oauthToken.getAccessToken();
        String payload = "{\"batchId\":\"" + batchId + "\"}";
        return Objects.requireNonNull(webClient.post()
                .uri(apiUrl).header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error(errorBody);
                                    return Mono.empty();
                                })
                )
                .bodyToMono(NonRealTimeBatch.class)
                .block());

    }


    public Object checkByBatchId(String batchId) {
        String apiUrl = url + "/api/getnchlipstxnlistbybatchid";
        String accessToken = oauthToken.getAccessToken();
        String payload = "{\"batchId\":\"" + batchId + "\"}";
        return Objects.requireNonNull(webClient.post()
                .uri(apiUrl).header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error(errorBody);
                                    return Mono.empty();
                                })
                )
                .bodyToMono(Object.class)
                .block());

    }
}
