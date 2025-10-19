package com.fcgo.eft.sutra.dto.req;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestNew {
    private String batchId;
    private String batchAmount;
    private String batchCount;
    private String batchCrncy;
    private String categoryPurpose;
    private String debtorAgent;
    private String debtorBranch;
    private String debtorName;
    private String debtorAccount;
    private long poCode;
    private String accountCd;
    private String poRequestNo;
    private List<EftPaymentRequestDetailReq> details;

    @Override
    public String toString() {
        return "{\"batchId\":\"" + batchId +
                "\",\"batchAmount\":\"" + batchAmount +
                "\",\"batchCount\":\"" + batchCount +
                "\",\"batchCrncy\":\"" + batchCrncy +
                "\",\"categoryPurpose\":\"" + categoryPurpose +
                "\",\"debtorAgent\":\"" + debtorAgent +
                "\",\"debtorBranch\":\"" + debtorBranch +
                "\",\"debtorName\":\"" + debtorName +
                "\",\"debtorAccount\":\"" + debtorAccount +
                "\",\"poCode\":\"" + poCode + "\"}";
    }
}
