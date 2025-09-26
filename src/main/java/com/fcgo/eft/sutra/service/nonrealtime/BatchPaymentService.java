package com.fcgo.eft.sutra.service.nonrealtime;


import com.fcgo.eft.sutra.dto.req.CipsFundTransfer;

import java.math.BigInteger;

public interface BatchPaymentService {
    void start(CipsFundTransfer cipsFundTransfer, BigInteger masterId);

    void setCount(int count);

    int getCount();
}
