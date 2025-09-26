package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeCheckStatusByDate;
import com.fcgo.eft.sutra.service.realtime.RealTimeStatusFromNchl;
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
    private final ExecutorService executorService;
    private final RealTimeStatusFromNchl getRealTimeStatus;

    @Scheduled(cron = "0 10 10,12,14,15,16,17,18 * * *")
    public void executeCheckTransactionStatus() {
            repository.findByPendingDate().forEach(date -> {
                nonRealTime.nonRealtimeCheckUpdate(date);
                realTime.realTimeCheckByDate(date);
            });
//            repository.findByPendingTransactionId().forEach(getRealTimeStatus::getRealTimeByBatch);
            repository.findByPushed("N").forEach(statusUpdate::update);

    }

//    @Scheduled(cron = "0 45 * * * *")
    public void executeEveryHour55Minute() {
        try {
            executorService.submit(() -> {
                repository.findByPendingTransactionId().forEach(transactionId -> {
                    try {
                        getRealTimeStatus.getRealTimeByBatch(transactionId);
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                });

                repository.findByPushed("N").forEach(statusUpdate::update);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
