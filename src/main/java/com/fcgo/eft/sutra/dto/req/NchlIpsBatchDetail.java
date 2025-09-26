package com.fcgo.eft.sutra.dto.req;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NchlIpsBatchDetail {
    private String batchId;
    private BigDecimal batchAmount;
    private Integer batchCount;
    private String batchCrncy;
    //eg RTPS ECPG
    private String categoryPurpose;
    private String debtorAgent;
    private String debtorBranch;
    private String debtorName;
    private String debtorAccount;

    @Override
    public String toString() {
        return "{\"batchId\":\"" + batchId + "\",\"batchAmount\":\"" + batchAmount + "\",\"batchCount\":\"" + batchCount + "\",\"batchCrncy\":\"" + batchCrncy + "\",\"categoryPurpose\":\"" + categoryPurpose + "\",\"debtorAgent\":\"" + debtorAgent + "\",\"debtorBranch\":\"" + debtorBranch + "\",\"debtorName\":\"" + debtorName + "\",\"debtorAccount\":\"" + debtorAccount + "\"}";
    }
}
