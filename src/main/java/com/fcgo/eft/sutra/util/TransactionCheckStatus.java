package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.BankHeadOfficeRepository;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.service.*;
import com.fcgo.eft.sutra.service.impl.SuTRAProcessingStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCheckStatus {

    private final NonRealTimeCheckStatusService nonRealTime;
    private final RealTimeCheckStatusService realTime;
    private final NchlReconciledRepository repository;
    private final TransactionStatusUpdate statusUpdate;
    private final BankAccountDetailsService bankAccountDetailsService;
    private final BankHeadOfficeService bankHeadOfficeService;
    private final BankHeadOfficeRepository headOfficeRepository;
    private final PaymentReceiveService bankMapService;
    private final EftPaymentReceiveService paymentReceiveService;
    private final SuTRAProcessingStatus suTRAProcessingStatus;
    private final AccEpaymentRepository epaymentRepository;
    private final IsProdService isProdService;
    private final ThreadPoolExecutor executor;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


    @PostConstruct
    public void executePostConstruct() {
        bankHeadOfficeService.setHeadOfficeId();
        bankMapService.setBankMaps(headOfficeRepository.findBankMap());
        repository.findByPushed("N").forEach(statusUpdate::update);
        isProdService.init();
        if (isProdService.isProdService()) {
            executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build()));
        }
    }


    @Scheduled(cron = "0 15 08,10,12,14,16,20,23 * * *")
    public void executeCheckTransactionStatus() {
        if (!isProdService.isProdService()) {
            return;
        }
        long startTime = 20251016000000L;
        long dateTime = Long.parseLong(dateFormat.format(new Date())) - (50000 * 4);

        repository.findByPendingDate().forEach(yyyyMMdd -> {
            String year = yyyyMMdd.substring(0, 4);
            String month = yyyyMMdd.substring(4, 6);
            String day = yyyyMMdd.substring(6, 8);
            String date = year + "-" + month + "-" + day;
            log.info("{} Non Real Time Status", date);
            nonRealTime.checkStatusByDate(date);
            log.info("Non Real Time Status Completed {} Real Time Status", date);
            realTime.checkStatusByDate(date);
            log.info("Non Real Time Status Completed {}", date);
            repository.updateMissingStatusSent();
            repository.findByPushed("N").forEach(statusUpdate::update);
            epaymentRepository.updateSuccessEPayment().forEach(suTRAProcessingStatus::check);
            if (dateTime > startTime) {
                headOfficeRepository.updatePaymentPendingStatusDetail(startTime, dateTime);
                headOfficeRepository.updatePaymentPendingStatusMaster(startTime, dateTime);
                headOfficeRepository.updatePaymentPendingStatusDetail();
            }
        });
        executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build()));
    }

    @Scheduled(cron = "0 50 10,20 * * *")
    public void fetchBankAccountDetails() {
        if (!isProdService.isProdService()) {
            return;
        }
        bankAccountDetailsService.fetchBankAccountDetails();
    }
}
