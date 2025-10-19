package com.fcgo.eft.sutra.service;

import com.fcgo.eft.sutra.dto.req.BankMap;
import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.req.PaymentRequestNew;
import com.fcgo.eft.sutra.dto.res.PaymentSaved;
import com.fcgo.eft.sutra.security.AuthenticatedUser;

import java.util.List;
import java.util.Map;

public interface PaymentSaveService {
    Map<Long, Boolean> getStatus();

    Map<String, String> getBankMap();

    void setBankMaps(List<BankMap> bankMaps);

    void busy(long poCode, boolean busy);

    PaymentSaved save(EftPaymentReceive receive, AuthenticatedUser user);

    PaymentSaved save(PaymentRequestNew receive, AuthenticatedUser user);

}
