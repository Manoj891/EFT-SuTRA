package com.fcgo.eft.sutra.service.realtime;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CipsBatchDetail {
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

}
