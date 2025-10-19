package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.BankHeadOfficeRepository;
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
        isProdService.init();
        if (isProdService.isProdService()) {
            executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(0).build()));
        }
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void executeEvery15Min() {
        epaymentRepository.updateSuccessEPayment().forEach(suTRAProcessingStatus::check);
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
        long startTime = 20251016000000L;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        long dateTime = Long.parseLong(dateFormat.format(calendar.getTime())) - (50000 * 4);
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
        repository.findByPushed("N").forEach(statusUpdate::update);
        epaymentRepository.updateSuccessEPayment().forEach(suTRAProcessingStatus::check);
        if (dateTime > startTime) {
            headOfficeRepository.updatePaymentPendingStatusDetail(startTime, dateTime);
            headOfficeRepository.updatePaymentPendingStatusDetail();
            headOfficeRepository.updatePaymentPendingStatusMaster(startTime, dateTime);

        }

        executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build()));
    }

    @Scheduled(cron = "0 50 21 * * *")
    public void fetchBankAccountDetails() {
        if (!isProdService.isProdService()) {
            return;
        }
        bankAccountDetailsService.fetchBankAccountDetails();
    }
}
