package com.fcgo.eft.sutra.service.nonrealtime;

import com.fcgo.eft.sutra.dto.PostCipsByDateResponseWrapper;
import com.fcgo.eft.sutra.dto.res.NchlIpsBatchDetailRes;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.service.impl.NchlReconciledService;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = RuntimeException.class)
public class NonRealTimeCheckStatusByDate {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final WebClient webClient;
    private final NchlReconciledService repository;


    public void nonRealtimeCheckUpdate(String date) {
        String apiUrl = url + "/api/getnchlipstxnlistbydate";
        String accessToken = oauthToken.getAccessToken();
        String payload = "{\"txnDateFrom\":\"" + date + "\",\"txnDateTo\":\"" + date + "\"} ";
        List<PostCipsByDateResponseWrapper> list = Objects.requireNonNull(webClient.post().uri(apiUrl).header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<List<PostCipsByDateResponseWrapper>>() {
        }).block());
        list.forEach(response -> {
            NchlIpsBatchDetailRes td = response.getNchlIpsBatchDetail();
            if (td != null && td.getDebitStatus() != null && td.getDebitStatus().length() > 1) {


                response.getNchlIpsTransactionDetailList()
                        .forEach(detail -> {
                            try {
                                if (detail.getCreditStatus() != null && detail.getCreditStatus().length() > 1) {
                                    try {
                                        log.info("InstructionId: {} status: {} {}", detail.getInstructionId(), detail.getCreditStatus(), detail.getReasonDesc());
                                        String transactionId = "";
                                        try {
                                            transactionId = detail.getId();
                                            if (transactionId != null) {
                                                transactionId = "" + detail.getInstructionId();
                                            }
                                        } catch (Exception ignored) {
                                        }
                                        repository.save(detail.getInstructionId(), td.getDebitStatus(), td.getDebitReasonDesc(), detail.getCreditStatus(), detail.getReasonDesc(), transactionId, detail.getRecDate());

                                    } catch (Exception i) {
                                        log.info(i.getMessage());
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        });

            }
        });
    }

    public void nonRealtimeCheckUpdate(String dateFrom, String dateTo) {
        String apiUrl = url + "/api/getnchlipstxnlistbydate";
        String accessToken = oauthToken.getAccessToken();
        String payload = "{\"txnDateFrom\":\"" + dateFrom + "\",\"txnDateTo\":\"" + dateTo + "\"} ";
        List<PostCipsByDateResponseWrapper> list = Objects.requireNonNull(webClient.post().uri(apiUrl).header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<List<PostCipsByDateResponseWrapper>>() {
        }).block());
        list.forEach(response -> {
            NchlIpsBatchDetailRes td = response.getNchlIpsBatchDetail();
            if (td != null && td.getDebitStatus() != null && td.getDebitStatus().length() > 1) {


                response.getNchlIpsTransactionDetailList()
                        .forEach(detail -> {
                            try {
                                if (detail.getCreditStatus() != null && detail.getCreditStatus().length() > 1) {
                                    try {
                                        log.info("InstructionId: {} status: {} {}", detail.getInstructionId(), detail.getCreditStatus(), detail.getReasonDesc());
                                        String transactionId = "";
                                        try {
                                            transactionId = detail.getId();
                                            if (transactionId != null) {
                                                transactionId = "" + detail.getInstructionId();
                                            }
                                        } catch (Exception ignored) {
                                        }
                                        repository.save(detail.getInstructionId(), td.getDebitStatus(), td.getDebitReasonDesc(), detail.getCreditStatus(), detail.getReasonDesc(), transactionId, detail.getRecDate());

                                    } catch (Exception i) {
                                        log.info(i.getMessage());
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        });

            }
        });
    }
}
