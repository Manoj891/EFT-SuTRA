package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NchlIpsResponse {
    private String responseCode;
    private String responseDescription;
    private String billsPaymentDescription;
    private String billsPaymentResponseCode;
    private Object data; // You can change this to a specific type if you know the structure of "data"
    private List<Object> fieldErrors;

    @Override
    public String toString() {
        return "{\"responseCode\":\"" + responseCode + "\",\"responseDescription\":\"" + responseDescription + "\",\"billsPaymentDescription\":\"" + billsPaymentDescription + "\",\"billsPaymentResponseCode\":\"" + billsPaymentResponseCode + "\",\"data\":\"" + data + "\",\"fieldErrors\":\"" + fieldErrors + "\"}";
    }
}
