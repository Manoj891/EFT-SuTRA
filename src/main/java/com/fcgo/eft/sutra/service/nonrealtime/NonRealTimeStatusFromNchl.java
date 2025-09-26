package com.fcgo.eft.sutra.service.nonrealtime;


import com.fcgo.eft.sutra.token.NchlOauthToken;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NonRealTimeStatusFromNchl {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final WebClient webClient;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public Object checkByBatchNonRealTime(String batchId) {
        String apiUrl = url + "/api/getnchlipstxnlistbybatchid";
        String accessToken = oauthToken.getAccessToken();
        String payload = "{\"batchId\":\"" + batchId + "\"}";

        return webClient.post()
                .uri(apiUrl).header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CipsBatchResponse {
        private String batchId;
        private String recDate;
        private String batchCrncy;
        private String categoryPurpose;
        private String debtorAgent;
        private String debtorBranch;
        private String debtorName;
        private String debtorAccount;
        private String debitStatus;
        private String debitReasonCode;
        private String debitReasonDesc;
        private String txnResponse;
        private List<CipsTransactionDetail> cipsTransactionDetailList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CipsTransactionDetail {
        private String id;
        private String batchId;
        private String instructionId;
        private String endToEndId;

        private String creditorAgent;
        private String creditorBranch;
        private String creditorName;
        private String creditorAccount;

        private String creditStatus;

        private String remarks;
        private String particulars;
        private String reasonDesc;
        public String recDate;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class NchlIpsBatchDetailResponse {
        private long id;
        private String batchId;

        private long isoTxnId;
        private double batchAmount;
        private int batchCount;
        private double batchChargeAmount;
        private String batchCrncy;
        private String categoryPurpose;
        private String debtorAgent;
        private String debtorBranch;
        private String debtorName;
        private String debtorAccount;
        private String debtorPhone;
        private String debtorMobile;
        private String debtorEmail;
        private String debitStatus;
        private String debitReasonCode;
        private String debitReasonDesc;
        private String txnResponse;
        private List<NchlIpsTransactionDetail> nchlIpsTransactionDetailList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class NchlIpsTransactionDetail {
        private long id;
        private long batchId;
        private String instructionId;
        private String endToEndId;
        private String recDate;
        private String creditorAgent;
        private String creditorBranch;
        private String creditorName;
        private String creditorAccount;
        private String creditStatus;
        private String reasonCode;
        private String remarks;
        private String particulars;
        private String reasonDesc;
        private String txnResponse;
    }


}
