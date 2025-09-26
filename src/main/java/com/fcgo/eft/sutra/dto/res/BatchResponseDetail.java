package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchResponseDetail {
    private Long id;
    private Long batchId;
    private String isoTxnId;
    private Date recDate;
    private String instructionId;
    private String endToEndId;
    private String chargeLiability;
    private String purpose;
    private String merchantId;
    private String appId;
    private String appTxnId;
    private String creditorAgent;
    private String creditorBranch;
    private String creditorName;
    private String creditorAccount;
    private String creditStatus;
    private String reasonDesc;
}
