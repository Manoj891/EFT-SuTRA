package com.fcgo.eft.sutra.service;

import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;

public interface EftPaymentReceiveService {
    void paymentReceive(EftPaymentReceive receive);
}
