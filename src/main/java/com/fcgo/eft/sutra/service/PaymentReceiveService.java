package com.fcgo.eft.sutra.service;

import com.fcgo.eft.sutra.dto.req.BankMap;
import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;

import java.util.List;
import java.util.Map;

public interface PaymentReceiveService {
    void setBankMaps(List<BankMap> bankMaps);

    Map<String, String> getBankMap();

    PaymentReceiveStatus paymentReceive(EftPaymentReceive receive);
}
