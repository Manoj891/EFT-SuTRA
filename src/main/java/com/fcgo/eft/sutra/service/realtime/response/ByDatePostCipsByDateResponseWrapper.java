package com.fcgo.eft.sutra.service.realtime.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ByDatePostCipsByDateResponseWrapper {
    private RealTimeTransaction cipsBatchDetail;
    private List<RealTimeTransactionDetail> cipsTransactionDetailList;
  }
