package com.fcgo.eft.sutra.dto.nchlres;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RealTimeBatch {
    private Long id;
    private String batchId;
    private String recDate;

    private String debitStatus;
    private String debitReasonCode;
    private String ipsBatchId;
    private String fileName;

    private String settlementDate;
    private String debitReasonDesc;
    private String txnResponse;

    private List<RealTimeBatchDetail> nchlIpsTransactionDetailList;
}
