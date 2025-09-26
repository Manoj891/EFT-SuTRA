package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealTimeStatusById {
    private String recDate;
    private String batchCrncy;
    private String debitStatus;
    private String debitReasonDesc;
    private String txnResponse;
    private List<RealTimeStatusByIdDetail> cipsTransactionDetailList;
}
