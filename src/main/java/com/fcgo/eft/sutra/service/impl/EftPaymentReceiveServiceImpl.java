package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.req.PaymentRequestNew;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeTransactionStart;
import com.fcgo.eft.sutra.service.realtime.RealTimeTransactionStart;
import com.fcgo.eft.sutra.util.IsProdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EftPaymentReceiveServiceImpl implements EftPaymentReceiveService {
    private final PaymentReceiveService service;
    private final IsProdService isProdService;
    private final NonRealTimeTransactionStart nonRealTimeThread;
    private final RealTimeTransactionStart realTimeThread;
    @Value("${server.port}")
    private String port;

    @Override
    public void paymentReceive(EftPaymentReceive receive) {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            startTransactionThread(service.paymentReceive(receive));
        } else {
            throw new CustomException("Prod service not supported");
        }
    }

    @Override
    public void paymentReceive(PaymentRequestNew receive) {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            startTransactionThread(service.paymentReceive(receive));
        } else {
            throw new CustomException("Prod service not supported");
        }
    }

    @Override
    public void startTransactionThread(PaymentReceiveStatus status) {
        System.out.println("Payment Starting................"+status.getOnus()+" "+isProdService.isProdService() );
        if (isProdService.isProdService()) {
//            try {
//                if (status.getOffus() > 0) {
//                    if (!nonRealTimeThread.isStarted()) {
//                        nonRealTimeThread.start();
//                    }
//                }
//            } catch (Exception e) {
//                log.info(e.getMessage());
//            }

            try {
                if (status.getOnus() > 0) {
                    if (!realTimeThread.isStarted()) {
                        realTimeThread.start();
                    }
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        } else {
            throw new CustomException("Prod service not supported");
        }
    }
}
