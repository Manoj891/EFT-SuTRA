package com.fcgo.eft.sutra.dto.req;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EftPaymentReceive {
    private PaymentRequest paymentRequest;
    private List<EftPaymentRequestDetailReq> eftPaymentRequestDetail;

    @Override
    public String toString() {
        return "{\"paymentRequest\":\"" + paymentRequest + "\",\"eftPaymentRequestDetail\":\"" + eftPaymentRequestDetail + "\"}\n";
    }
}
