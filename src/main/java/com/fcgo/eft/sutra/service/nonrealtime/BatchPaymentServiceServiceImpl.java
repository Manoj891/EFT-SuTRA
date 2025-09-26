package com.fcgo.eft.sutra.service.nonrealtime;

import com.fcgo.eft.sutra.dto.req.CipsFundTransfer;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import com.fcgo.eft.sutra.token.TokenGenerate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class BatchPaymentServiceServiceImpl implements BatchPaymentService {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final TokenGenerate tokenGenerate;
    private final WebClient webClient;
    private final EftBatchPaymentDetailRepository repository;

    @Override
    public void start(CipsFundTransfer cipsFundTransfer, BigInteger masterId) {
        String batchId = cipsFundTransfer.getNchlIpsBatchDetail().getBatchId();
        log.info("SENDING NCHL IPS POST BATCH REQUEST {}", batchId);
        String apiUrl = url + "/api/postnchlipsbatch";
        String accessToken = oauthToken.getAccessToken();
        if (Objects.isNull(accessToken)) {
            throw new RuntimeException("Access token is null: NCHL connection issue.");
        }
        try {
            cipsFundTransfer.setToken(tokenGenerate.generateHash(cipsFundTransfer));
            String res = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(cipsFundTransfer)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .doOnNext(errorBody -> log.error("Error Code {} received from NCHL: {}", clientResponse.statusCode(), errorBody))
                                    .then(Mono.empty()) // <-- no exception, just complete
                    )
                    .bodyToMono(String.class)
                    .block();

            assert res != null;
            repository.updateBatchBuild("SENT", masterId);
            /*
            if (res.getCipsBatchResponse().getDebitStatus().equals("000")) {

                repository.updateBatchBuild(masterId);
                res.getCipsTxnResponseList().forEach(d -> {
                    long eftNo = Long.parseLong(d.getInstructionId());
                    reconciledRepository.save(NchlReconciled.builder()
                            .instructionId(eftNo)
                            .debitStatus("000")
                            .debitMessage(res.getCipsBatchResponse().getResponseMessage())
                            .creditStatus(d.getCreditStatus())
                            .creditMessage(d.getResponseMessage())
                            .recDate(new Date())
                            .transactionId(String.valueOf(d.getId()))
                            .pushed("N")
                            .build());
                });
            }
            */
            log.info("PUSHED INTO NCHL POST BATCH ID: {} | Batch size: {}", batchId, cipsFundTransfer.getNchlIpsTransactionDetailList().size());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
