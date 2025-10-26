package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.BankHeadOfficeRepository;
import com.fcgo.eft.sutra.repository.oracle.EftNchlRbbBankMappingRepository;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.service.*;
import com.fcgo.eft.sutra.service.impl.SuTRAProcessingStatus;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeCheckStatusService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private final EftNchlRbbBankMappingRepository eftNchlRbbBankMappingRepository;
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
        bankMapService.setBankMaps(eftNchlRbbBankMappingRepository.findBankMap());
        isProdService.init();
        if (isProdService.isProdService()) {
            executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(0).build()));
        }
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void executeEvery30Min() {
        long startTime = 20251018000000L;
        long dateTime = Long.parseLong(dateFormat.format(new Date())) - (3000);

        headOfficeRepository.updatePaymentPendingStatusDetail(startTime, dateTime);
        headOfficeRepository.updatePaymentPendingStatusMaster(startTime, dateTime);
        headOfficeRepository.updatePaymentPendingStatusDetail();


        epaymentRepository.updateSuccessEPayment().forEach(suTRAProcessingStatus::check);
        repository.updateMissingStatusSent();
        executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build()));
        executor.submit(() -> repository.findByPushed("N").forEach(statusUpdate::update));

    }

    @Scheduled(cron = "0 15 08,12,16,20,22 * * *")
    public void executeStatus() {
        executor.submit(() -> {
            repository.findRealTimePendingInstructionId().forEach(instructionId -> {
                realTime.checkStatusByInstructionId(instructionId);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            });

            repository.findNonRealTimePendingBatchId().forEach(batchId -> {
                nonRealTime.checkStatusByBatchId(batchId);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            });
            repository.updateMissingStatusSent();
            repository.findByPushed("N").forEach(statusUpdate::update);
        });
    }

    @Scheduled(cron = "0 05 00 * * *")
    public void executeCheckTransactionStatus() {
        if (!isProdService.isProdService()) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        String yyyyMMdd = dateFormat.format(calendar.getTime());
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
    }

    @Scheduled(cron = "0 50 21 * * *")
    public void fetchBankAccountDetails() {
        if (!isProdService.isProdService()) {
            return;
        }
        bankAccountDetailsService.fetchBankAccountDetails();
    }
}
