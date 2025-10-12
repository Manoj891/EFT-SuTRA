package com.fcgo.eft.sutra.service.realtime.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ByDatePostCipsByDateResponseWrapper {
    private ByDateCipsBatchDetail cipsBatchDetail;
    private List<ByDateCipsTransactionDetail> cipsTransactionDetailList;
  }
