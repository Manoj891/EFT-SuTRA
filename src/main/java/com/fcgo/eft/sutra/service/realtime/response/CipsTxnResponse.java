package com.fcgo.eft.sutra.service.realtime.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CipsTxnResponse {
    private String responseCode;
    private String responseMessage;
    private long id;
    private String instructionId;
    private String creditStatus;
}
