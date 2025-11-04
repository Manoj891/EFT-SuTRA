package com.fcgo.eft.sutra.service.realtime;


import com.fcgo.eft.sutra.dto.res.EftPaymentRequestDetailProjection;

public interface RealTimeTransactionService {
    void pushPayment(EftPaymentRequestDetailProjection m, String creditorBranch);
}
