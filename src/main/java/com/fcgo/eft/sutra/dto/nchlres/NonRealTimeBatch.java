package com.fcgo.eft.sutra.dto.nchlres;

import com.fcgo.eft.sutra.dto.res.NchlIpsTransactionDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NonRealTimeBatch {
    private Long id;
    private String batchId;
    private String recDate;
    private String isoTxnId;
    private String batchAmount;
    private String batchCount;
    private String batchCrncy;
    private String categoryPurpose;
    private String debtorAgent;
    private String debtorBranch;
    private String debtorName;
    private String debtorAccount;
    private String debitStatus;
    private String rcreTime;
    private String debitReasonDesc;
    private String debitReasonCode;
    private String settlementDate;
    private String txnResponse;
    private List<NchlIpsTransactionDetail> nchlIpsTransactionDetailList;
}
