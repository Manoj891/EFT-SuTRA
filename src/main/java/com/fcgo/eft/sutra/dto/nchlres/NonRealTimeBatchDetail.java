package com.fcgo.eft.sutra.dto.nchlres;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NonRealTimeBatchDetail {
    private String id;
    private String batchId;
    private Date recDate;
    private Long instructionId;
    private String endToEndId;
    private String creditorAgent;
    private Integer creditorBranch;
    private String creditorName;
    private String creditorAccount;
    private String creditStatus;
    private String reasonCode;
    private String reversalStatus;
    @Getter(AccessLevel.NONE)
    private String reasonDesc;
    private String txnResponse;

    public String getReasonDesc() {
        return reasonDesc == null ? "" : reasonDesc;
    }
}
