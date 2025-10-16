package com.fcgo.eft.sutra.dto.res;

import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSaved {
    private List<EftBatchPaymentDetail> details;
    private int offus = 0;
    private int onus = 0;
}
