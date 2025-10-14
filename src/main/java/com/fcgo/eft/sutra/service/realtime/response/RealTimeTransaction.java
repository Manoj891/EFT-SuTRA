package com.fcgo.eft.sutra.service.realtime.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealTimeTransaction {
    private Long id;
    private String batchId;
    private String recDate;
    private String batchCrncy;
    private String categoryPurpose;
    private String debtorAgent;
    private String debtorBranch;
    private String debtorName;
    private String debtorAccount;
    private String debitStatus;
    private String debitReasonDesc;
    private String settlementDate;
    private String txnResponse;
}
