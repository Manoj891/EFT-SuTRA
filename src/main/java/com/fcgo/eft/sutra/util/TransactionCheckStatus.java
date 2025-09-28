package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.dto.nchlres.NonRealTimeBatch;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.repository.oracle.BankHeadOfficeRepository;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.service.BankAccountDetailsService;
import com.fcgo.eft.sutra.service.BankHeadOfficeService;
import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeCheckStatusByDate;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeStatusFromNchl;
import com.fcgo.eft.sutra.service.realtime.RealTimeStatusFromNchl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCheckStatus {

    private final NonRealTimeCheckStatusByDate nonRealTime;
    private final RealTimeStatusFromNchl realTime;
    private final NchlReconciledRepository repository;
    private final TransactionStatusUpdate statusUpdate;
    private final BankAccountDetailsService bankAccountDetailsService;
    private final BankHeadOfficeService bankHeadOfficeService;
    private final BankHeadOfficeRepository headOfficeRepository;
    private final PaymentReceiveService bankMapService;
    private final EftPaymentReceiveService paymentReceiveService;

    private final NonRealTimeStatusFromNchl nonRealTimeStatusFromNchl;
    private final NonRealTimeCheckStatusByDate checkByBatchNonRealTime;

    @PostConstruct
    public void executePostConstruct() {
        bankHeadOfficeService.setHeadOfficeId();
        bankMapService.setBankMaps(headOfficeRepository.findBankMap());
//        new Thread(this::updateNonRealTimeStatus).start();

        repository.findByPushed("N").forEach(statusUpdate::update);


//        headOfficeRepository.updatePaymentPendingStatusDetail();
//        headOfficeRepository.updatePaymentPendingStatusMaster();
//        new Thread(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build())).start();

    }

    private void updateNonRealTimeStatus() {
        repository.findByNonRealTimePendingTransactionId().forEach(batchId -> {
            log.info("Fetching Batch Id: {}", batchId);
            NonRealTimeBatch nonRealTimeBatch = nonRealTimeStatusFromNchl.checkByBatchNonRealTime(batchId);
            checkByBatchNonRealTime.updateNonRealTimeStatus(nonRealTimeBatch);
            log.info("Updating SuTRA---------------");
            repository.findByPushed("N").forEach(statusUpdate::update);
        });

    }

    //    @Scheduled(cron = "0 10 10,12,14,15,16,17,18 * * *")
    public void executeCheckTransactionStatus() {
        headOfficeRepository.updatePaymentPendingStatusDetail();
        headOfficeRepository.updatePaymentPendingStatusMaster();
        repository.findByPendingDate().forEach(date -> {
            nonRealTime.nonRealtimeCheckUpdate(date);
            realTime.realTimeCheckByDate(date);
        });
        repository.findByPushed("N").forEach(statusUpdate::update);
    }

    //    @Scheduled(cron = "0 0 10,16,20 * * *")
    public void fetchBankAccountDetails() {
        bankAccountDetailsService.fetchBankAccountDetails();
    }
}
