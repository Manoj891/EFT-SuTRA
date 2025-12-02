package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.dto.res.NchlReconciledRes;
import com.fcgo.eft.sutra.dto.res.ReconciledUpdateReq;
import com.fcgo.eft.sutra.repository.NchlReconciledRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TransactionStatusUpdate {
    private final NchlReconciledRepository repository;
    private final WebClient webClient;
    private final StringToJsonNode jsonNode;
    @Getter
    private String token;
    @Getter
    private boolean started = false;

    public void init() {
        token = webClient.post()
                .uri("https://sutrav3.fcgo.gov.np/SuTRAv3/public/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"userId\":\"1\",\"sessionId\":\"1\",\"orgId\":\"1\",\"adminid\":\"1\"}")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    public synchronized void statusUpdateApi() {
        long datetime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date()));
        try {
            for (int i = 0; i < 100; i++) {
                List<NchlReconciledRes> list = repository.findByPushed(datetime - 3000);
                if (list.isEmpty()) {
                    break;
                }
                started = true;
                List<ReconciledUpdateReq> res = webClient.post()
                        .uri("https://sutrav3.fcgo.gov.np/SuTRAv3/utility/eft-status")
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(list)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .flatMap(errorBody -> {
                                            log.info("Remote error: {}", errorBody);
                                            return Mono.error(new RuntimeException("Remote API returned error"));
                                        })
                        )
                        .bodyToMono(new ParameterizedTypeReference<List<ReconciledUpdateReq>>() {
                        })
                        .block();
                assert res != null;
                res.forEach(d -> {
                    log.info("{} {}", d.getPushed(), d.getInstructionId());
                    repository.updateStatus(d.getPushed(), datetime, d.getInstructionId());
                });
                try {
                    Thread.sleep(60000);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            log.error("Error during status update {}", e.getMessage());
        }
        started = false;
    }

}
