package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.entity.oracle.PoCodeMapped;
import com.fcgo.eft.sutra.repository.oracle.BankHeadOfficeRepository;
import com.fcgo.eft.sutra.repository.oracle.EftNchlRbbBankMappingRepository;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.repository.oracle.PoCodeMappedRepository;
import com.fcgo.eft.sutra.service.*;
import com.fcgo.eft.sutra.service.impl.PoCodeMappedService;
import com.fcgo.eft.sutra.service.impl.SuTRAProcessingStatus;
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
    private final SuTRAProcessingStatus suTRAProcessingStatus;
    private final DB2nd epaymentRepository;
    private final IsProdService isProdService;
    private final ThreadPoolExecutor executor;
    private final LoginService loginService;
    private final StringToJsonNode jsonNode;
    private final PoCodeMappedService poCodeMappedService;
    @Value("${server.port}")
    private String port;


    @PostConstruct
    public void executePostConstruct() {
        poCodeMappedService.setDate();
        bankHeadOfficeService.setHeadOfficeId();
        bankMapService.setBankMaps(eftNchlRbbBankMappingRepository.findBankMap());
        isProdService.init();
        loginService.init();
        tryForNextAttempt();
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build()));
        }
    }

    @Scheduled(cron = "0 30 08,09,10,11,12,13,14,15,16,17,18,20,22 * * *")
    public void executeEveryHour30Min() {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            long startTime = 20251018000000L;
            long dateTime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date())) - (12000);

            headOfficeRepository.updatePaymentPendingStatusDetail(startTime, dateTime);
            headOfficeRepository.updatePaymentPendingStatusMaster(startTime, dateTime);
            headOfficeRepository.updatePaymentPendingStatusDetail();

            epaymentRepository.updateSuccessEPayment().forEach(suTRAProcessingStatus::check);
            repository.updateMissingStatusSent();
            tryForNextAttempt();
//            tryTimeOutToReject();

            if (!statusUpdate.isStarted()) {
                statusUpdate.statusUpdate();
            }
            executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build()));
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
                tryForNextAttempt();
//                tryTimeOutToReject();
                repository.updateMissingStatusSent();
                if (!statusUpdate.isStarted()) {
                    statusUpdate.statusUpdate();
                }
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
            repository.updateMissingStatusSent();
            tryForNextAttempt();
//            tryTimeOutToReject();
        }
    }

    @Scheduled(cron = "0 01 02 * * *")
    public void fetchBankAccountDetails() {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            bankAccountDetailsService.fetchBankAccountDetails();
        }
    }

    private void tryForNextAttempt() {
        repository.missingStatusSent();
        repository.findTryForNextAttempt().forEach(id -> {
            repository.missingStatusSent(id);
            log.info("{} Trying For Next Attempt", id);
        });
    }

    public void tryTimeOutToReject() {
        repository.findTryTimeOutToReject().forEach(m -> {
            long instructionId = Long.parseLong(m.get("INSTRUCTION_ID").toString());
            String message = m.get("CREDIT_MESSAGE").toString();
            message = (message.substring(0, message.indexOf(". We will try again"))) + " Reject after " + m.get("TRY_COUNT") + " times on " + m.get("TRY_TIME") + ".";
            repository.updateRejectTransaction("1000", message, "997", "Reject", instructionId);
            log.info("Reject Transaction: {}", instructionId);
        });
    }
}
