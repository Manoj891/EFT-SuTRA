package com.fcgo.eft.sutra.dto.req;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCipsBatchResponse {
    private CipsBatchResponse cipsBatchResponse;
    private List<CipsTxnResponseList> cipsTxnResponseList;
}
