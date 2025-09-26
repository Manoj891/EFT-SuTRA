package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NchlIpsTransactionDetail {
    private String id;
    private Long batchId;
    private Date recDate;
    private Long instructionId;
    private String endToEndId;
    private Double amount;
    private Double chargeAmount;
    private String creditorAgent;
    private String creditorBranch;
    private String creditorName;
    private String creditorAccount;
    private String creditStatus;
    private String reasonCode;
    private String reversalStatus;
    private String reasonDesc;
    private String txnResponse;
}
