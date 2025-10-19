package com.fcgo.eft.sutra.service;

import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.req.PaymentRequestNew;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;

public interface EftPaymentReceiveService {
    void startTransactionThread(PaymentReceiveStatus status);

    void paymentReceive(EftPaymentReceive receive);

    void paymentReceive(PaymentRequestNew receive);
}
