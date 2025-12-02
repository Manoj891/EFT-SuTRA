package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.repository.BankHeadOfficeRepository;
import com.fcgo.eft.sutra.repository.EftNchlRbbBankMappingRepository;
import com.fcgo.eft.sutra.repository.NchlReconciledRepository;
import com.fcgo.eft.sutra.service.*;
import com.fcgo.eft.sutra.service.impl.AccountWhiteListSave;
import com.fcgo.eft.sutra.service.impl.PoCodeMappedService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeCheckStatusService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    private final IsProdService isProdService;
    private final ThreadPoolExecutor executor;
    private final LoginService loginService;
    private final StringToJsonNode jsonNode;
    private final PoCodeMappedService poCodeMappedService;
    private final AccountWhiteListSave accountWhiteListSave;
    @Value("${server.port}")
    private String port;

    @PostConstruct
    public void executePostConstruct() {
        statusUpdate.init();
        poCodeMappedService.setDate();
        bankHeadOfficeService.setHeadOfficeId();
        bankMapService.setBankMaps(eftNchlRbbBankMappingRepository.findBankMap());
        isProdService.init();
        loginService.init();
        new Thread(accountWhiteListSave::pushInSuTRA).start();
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void updateStatus() {
        if (!statusUpdate.isStarted() && isProdService.isStarted()) {
            statusUpdate.statusUpdateApi();
        }
    }


    @Scheduled(cron = "0 15 08,12,16,20,22 * * *")
    public void executeStatus() {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            executor.submit(() -> {
                repository.findRealTimePendingInstructionId().forEach(instructionId -> {
                    realTime.checkStatusByInstructionId(instructionId, 0);
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
                long nowTime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date()));
                repository.updateMissingStatusSent(nowTime);

            });
        }
    }

    @Scheduled(cron = "0 05 00 * * *")
    public void executeCheckTransactionStatus() {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -1);
            String date = jsonNode.getDateFormat().format(calendar.getTime());
            log.info("{} Non Real Time Status", date);
            nonRealTime.checkStatusByDate(date);
            log.info("Non Real Time Status Completed {} Real Time Status", date);
            realTime.checkStatusByDate(date);
            log.info("Non Real Time Status Completed {}", date);

            long startTime = 20251018000000L;
            long dateTime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date())) - (12000);
            long nowTime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date()));

            headOfficeRepository.updatePaymentPendingToSent(nowTime, startTime, dateTime);
            headOfficeRepository.updatePaymentPendingStatusDetail(startTime, dateTime);
            headOfficeRepository.updatePaymentPendingStatusMaster();
            repository.updateMissingStatusSent(nowTime);
            executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build()));

        }
    }

    @Scheduled(cron = "0 01 02 * * *")
    public void fetchBankAccountDetails() {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            bankAccountDetailsService.fetchBankAccountDetails();
        }
    }

}
