package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NchlIpsBatchDetailRes {
    private Long id;
    private String batchId;
    private String batchCrncy;
    private String categoryPurpose;
    private String debtorAgent;
    private String debtorBranch;
    private String debtorName;
    private String debtorAccount;
    private String debitStatus;
    private String debitReasonCode;
    private String settlementDate;
    private String debitReasonDesc;
    private String txnResponse;
    private int batchCount;
    private BigDecimal batchAmount;
    private List<NchlIpsTransactionDetail> nchlIpsTransactionDetailList;


}
