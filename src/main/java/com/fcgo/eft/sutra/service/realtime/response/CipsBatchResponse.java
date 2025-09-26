package com.fcgo.eft.sutra.service.realtime.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CipsBatchResponse {
    private String responseCode;
    private String responseMessage;
    private String batchId;
    private String debitStatus;
    private Long id;

}
