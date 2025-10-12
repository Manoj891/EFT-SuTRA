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

    private List<NchlIpsTransactionDetail> nchlIpsTransactionDetailList;
    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"batchId\":\"" + batchId + "\"," +
                "\"recDate\":\"" + recDate + "\"," +
                "\"isoTxnId\":\"" + isoTxnId + "\"," +
                "\"batchAmount\":\"" + batchAmount + "\"," +
                "\"batchCount\":\"" + batchCount + "\"," +
                "\"batchCrncy\":\"" + batchCrncy + "\"," +
                "\"categoryPurpose\":\"" + categoryPurpose + "\"," +
                "\"debtorAgent\":\"" + debtorAgent + "\"," +
                "\"debtorBranch\":\"" + debtorBranch + "\"," +
                "\"debtorName\":\"" + debtorName + "\"," +
                "\"debtorAccount\":\"" + debtorAccount + "\"," +
                "\"debitStatus\":\"" + debitStatus + "\"," +
                "\"rcreTime\":\"" + rcreTime + "\"," +
                "\"debitReasonDesc\":\"" + debitReasonDesc + "\"," +
                "\"nchlIpsTransactionDetailList\":" + (nchlIpsTransactionDetailList == null ? "[]" : nchlIpsTransactionDetailList.toString()) +
                "}";
    }


}
