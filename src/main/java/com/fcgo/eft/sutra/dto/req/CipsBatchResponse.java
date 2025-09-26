package com.fcgo.eft.sutra.dto.req;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CipsBatchResponse {
    private String responseCode;
    private String responseMessage;
    private String responseDescription;
    private String batchId;
    private String debitStatus;
    private String debitReasonDesc;
    private String debitReasonCode;
    private int id;

    private String settlementDate;

    @Override
    public String toString() {
        return "{\"responseCode\":\"" + responseCode + "\",\"responseMessage\":\"" + responseMessage + "\",\"responseDescription\":\"" + responseDescription + "\",\"batchId\":\"" + batchId + "\",\"debitStatus\":\"" + debitStatus + "\",\"debitReasonDesc\":\"" + debitReasonDesc + "\",\"debitReasonCode\":\"" + debitReasonCode + "\",\"settlementDate\":\"" + settlementDate + "\",\"id=" + id + "\"}";
    }
}
