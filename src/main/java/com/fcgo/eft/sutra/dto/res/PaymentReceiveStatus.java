package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentReceiveStatus {
    private int onus;
    private int offus;
}
