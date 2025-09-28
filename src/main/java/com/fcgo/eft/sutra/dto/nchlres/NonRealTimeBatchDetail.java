package com.fcgo.eft.sutra.dto.nchlres;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonRealTimeBatchDetail {
    private String id;
    private String batchId;
    private String recDate;
    private String instructionId;
    private String endToEndId;
    private String creditorAgent;
    private Integer creditorBranch;
    private String creditorName;
    private String creditorAccount;
    private String creditStatus;
    private String reasonCode;
    private String reversalStatus;
    private String reasonDesc;
    private String txnResponse;
}
