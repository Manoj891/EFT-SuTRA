package com.fcgo.eft.sutra.service.realtime;

import com.fcgo.eft.sutra.dto.res.EftPaymentRequestDetailProjection;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.service.BankHeadOfficeService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class RealTimeTransactionStart {
    private final EftBatchPaymentDetailRepository repository;
    private final RealTimeTransactionService service;
    private final BankHeadOfficeService ho;
    private final ThreadPoolExecutor executor;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    @Getter
    private boolean started = false;

    public RealTimeTransactionStart(@Qualifier("realTime") ThreadPoolExecutor executor, EftBatchPaymentDetailRepository repository, RealTimeTransactionService service, BankHeadOfficeService ho) {
        this.repository = repository;
        this.service = service;
        this.ho = ho;
        this.executor = executor;
    }

    public void start() {
        while (true) {
            long start = Long.parseLong(sdf.format(new Date())) - 500;
            List<EftPaymentRequestDetailProjection> list = repository.findRealTimePending();
            list.addAll(repository.findRealTimePending(start));
            if (list.isEmpty()) {
                started = false;
                break;
            }
            started = true;

            list.forEach(d -> {
                try {
                    long time = Long.parseLong(sdf.format(new Date()));
                    repository.updateRealTimeTransactionStatus("BUILD", time, (d.getTryCount() + 1), d.getInstructionId());
                    executor.submit(() -> service.pushPayment(d, ho.getHeadOfficeId(d.getCreditorAgent())));
                } catch (Exception e) {
                    log.error("Real Time Transaction Start ERROR:{}", e.getMessage());
                }
            });
            int activeThread = executor.getActiveCount();
            while (activeThread > 5) {
                int sleep;
                if (activeThread > 35) {
                    sleep = 30000;
                } else if (activeThread > 25) {
                    sleep = 25000;
                } else if (activeThread > 15) {
                    sleep = 15000;
                } else if (activeThread > 12) {
                    sleep = 12000;
                } else if (activeThread > 10) {
                    sleep = 10000;
                } else {
                    sleep = 5000;
                }
                try {
                    log.info("Realtime process waiting for clearing pool — active threads: {}, waiting for {} seconds.", activeThread, sleep / 1000);
                    Thread.sleep(sleep);
                } catch (InterruptedException ignored) {
                }
                activeThread = executor.getActiveCount();

            }
        }
    }
}
