package com.fcgo.eft.sutra.service.realtime.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealTimeResponse {


    private CipsBatchResponse cipsBatchResponse;
    private List<CipsTxnResponse> cipsTxnResponseList;
}
