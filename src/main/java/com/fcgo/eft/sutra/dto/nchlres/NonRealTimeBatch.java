package com.fcgo.eft.sutra.dto.nchlres;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NonRealTimeBatch { private Long id;
    private String batchId;
    private Long isoTxnId;
    private Integer debitStatus;
    private String debitReasonCode;
    private String settlementDate;
    private String debitReasonDesc;
    private String txnResponse;
    private List<NonRealTimeBatchDetail> nchlIpsTransactionDetailList;
}
