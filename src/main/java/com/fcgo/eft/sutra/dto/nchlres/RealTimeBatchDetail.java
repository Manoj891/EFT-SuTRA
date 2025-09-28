package com.fcgo.eft.sutra.dto.nchlres;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealTimeBatchDetail {
    private Long id;
    private String batchId;
    private String isoTxnId;
    private String recDate;
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
    private String reasonCode;
    private String reversalStatus;
    private String refId;
    private String remarks;
    private String particulars;

    private String ipsTxnId;
    private String reasonDesc;
    private String txnResponse;
}
