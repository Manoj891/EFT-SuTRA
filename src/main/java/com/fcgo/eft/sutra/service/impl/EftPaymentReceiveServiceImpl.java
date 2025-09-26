package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.repository.oracle.BankHeadOfficeRepository;
import com.fcgo.eft.sutra.service.BankHeadOfficeService;
import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeTransactionStart;
import com.fcgo.eft.sutra.service.realtime.RealTimeTransactionStart;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EftPaymentReceiveServiceImpl implements EftPaymentReceiveService {
    private final PaymentReceiveService service;
    private final NonRealTimeTransactionStart nonRealTimeThread;
    private final RealTimeTransactionStart realTimeThread;
    private final BankHeadOfficeService bankHeadOfficeService;
    private final BankHeadOfficeRepository headOfficeRepository;
    private final PaymentReceiveService bankMapService;

    @Override
    public void paymentReceive(EftPaymentReceive receive) {
        startTransactionThread(service.paymentReceive(receive));
    }

    private void startTransactionThread(PaymentReceiveStatus status) {
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

    @PostConstruct
    public void executePostConstruct() {
        bankHeadOfficeService.setHeadOfficeId();
        bankMapService.setBankMaps(headOfficeRepository.findBankMap());
        headOfficeRepository.updatePaymentPendingStatusDetail();
        headOfficeRepository.updatePaymentPendingStatusMaster();
        new Thread(() -> startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build())).start();

    }
}
