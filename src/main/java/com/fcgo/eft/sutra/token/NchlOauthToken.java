package com.fcgo.eft.sutra.token;


import com.fcgo.eft.sutra.entity.oracle.NchlToken;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.service.impl.CacheService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Service
@Slf4j
public class NchlOauthToken {
    @Value("${nchl.npi.clientId}")
    private String cliendId;
    @Value("${nchl.npi.clientSecret}")
    private String clientSecret;
    @Value("${nchl.npi.username}")
    private String username;
    @Value("${nchl.npi.password}")
    private String password;
    @Value("${nchl.npi.grant_type}")
    private String grant_type;
    @Value("${nchl.npi.url}")
    private String url;
    @Autowired
    private WebClient webClient;
    @Autowired
    private CacheService cacheService;
    private NchlToken token;

    public String getAccessToken() {
        token = cacheService.findByKey();
        if (isValidToken()) {
            return token.getAccessToken();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        String token = getRefreshToken(this.token.getRefreshToken());
        if (token == null || token.isEmpty()) {
            throw new CustomException("NCHL token expired");
        }
        return token;

    }

    private boolean isValidToken() {
        try {
            if (token == null) {
                return false;
            }
            return (new Date().getTime() <= token.getExpiredAt().getTime());
        } catch (Exception e) {
            return false;
        }
    }


    private synchronized String getRefreshToken(String refreshToken) {
        try {
            String authentication = Base64.getEncoder().encodeToString(((cliendId + ":" + clientSecret).getBytes()));
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);

            token = webClient
                    .post()
                    .uri(url + "/oauth/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + authentication)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(NchlToken.class)
                    .block();

            if (token != null) {
                saveToken();
                return token.getAccess_token();
            }
            return callNCHLToken();
        } catch (Exception e) {
            return callNCHLToken();
        }
    }

    public synchronized String callNCHLToken() {
        if (isValidToken()) return token.getAccess_token();
        String authentication = Base64.getEncoder().encodeToString(((cliendId + ":" + clientSecret).getBytes()));
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", grant_type);
        formData.add("username", username);
        formData.add("password", password);

        try {
            token = webClient
                    .post()
                    .uri(url + "/oauth/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + authentication)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .doOnNext(errorBody -> {
                                        System.err.println("Status: " + clientResponse.statusCode());
                                        System.err.println("Error body: " + errorBody);
                                    })
                                    .then(Mono.empty())
                    )
                    .bodyToMono(NchlToken.class)
                    .block();

            if (token != null) {
                saveToken();
                return token.getAccess_token();
            }
        } catch (Exception e) {
            log.info("{}/oauth/token NCHL token could not be created: {}", url, e.getMessage());
        }
        return null;
    }

    private void saveToken() {
        Calendar cal = Calendar.getInstance();
        Date generatedAt = new Date();
        cal.setTime(generatedAt);
        cal.add(Calendar.SECOND, (token.getExpiresIn() - 60));
        token.setExpiredAt(cal.getTime());
        cacheService.save(token);
    }
}