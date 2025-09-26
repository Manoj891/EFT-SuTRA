package com.fcgo.eft.sutra.dto.req;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CipsTxnResponseList {
    private String responseCode;
    private String responseMessage;
    private int id;
    private String instructionId;
    private String creditStatus;

    @Override
    public String toString() {
        return "{\"responseCode\":\"" + responseCode + "\",\"responseMessage\":\"" + responseMessage + "\",\"id\":\"" + id + "\",\"instructionId\":\"" + instructionId + "\",\"creditStatus\":\"" + creditStatus + "\"}";
    }
}
