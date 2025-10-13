package com.fcgo.eft.sutra.service.realtime.response;

import com.fcgo.eft.sutra.entity.oracle.ReconciledTransaction;
import com.fcgo.eft.sutra.entity.oracle.ReconciledTransactionDetail;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ByDatePostCipsByDateResponseWrapper {
    private ReconciledTransaction cipsBatchDetail;
    private List<ReconciledTransactionDetail> cipsTransactionDetailList;
  }
