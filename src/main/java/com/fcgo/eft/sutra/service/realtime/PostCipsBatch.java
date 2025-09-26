package com.fcgo.eft.sutra.service.realtime;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCipsBatch {
    private CipsBatchDetail cipsBatchDetail;
    private List<CipsTransactionDetailList> cipsTransactionDetailList;
    private String token;
}
