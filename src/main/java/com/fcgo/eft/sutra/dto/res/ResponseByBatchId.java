package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseByBatchId {
    private Long id;
    private String batchId;
    private String isoTxnId;
    private String batchCrncy;
    private String categoryPurpose;
    private String debtorAgent;
    private String debtorBranch;
    private String debtorName;
    private String debtorAccount;
    private String debitStatus;
    private String debitReasonDesc;
    private List<BatchResponseDetail> nchlIpsTransactionDetailList;

}
