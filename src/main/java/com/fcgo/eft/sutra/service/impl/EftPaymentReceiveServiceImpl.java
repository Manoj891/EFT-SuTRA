package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeTransactionStart;
import com.fcgo.eft.sutra.service.realtime.RealTimeTransactionStart;
import com.fcgo.eft.sutra.util.IsProdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EftPaymentReceiveServiceImpl implements EftPaymentReceiveService {
    private final PaymentReceiveService service;
    private final IsProdService isProdService;
    private final NonRealTimeTransactionStart nonRealTimeThread;
    private final RealTimeTransactionStart realTimeThread;

    @Override
    public void paymentReceive(EftPaymentReceive receive) {
        if (!isProdService.isProdService()) throw new CustomException("Prod service not supported");
        service.paymentReceive(receive);
//        startTransactionThread();
    }

    public void startTransactionThread(PaymentReceiveStatus status) {
        try {
            if (status.getOffus() > 0) {
                if (!nonRealTimeThread.isStarted()) {
                    log.info("Non Real Time Thread is started...................");
                    nonRealTimeThread.start();
                } else {
                    log.info("Non Real Time Thread is already started...................");
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        try {
            if (status.getOnus() > 0) {
                if (!realTimeThread.isStarted()) {
                    log.info("Real Time Thread is started...................");
                    realTimeThread.start();
                } else {
                    log.info("Real Time Thread is already started...................");
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
