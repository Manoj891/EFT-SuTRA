package com.fcgo.eft.sutra.service.realtime;

import com.fcgo.eft.sutra.dto.res.EftPaymentRequestDetailProjection;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.service.BankHeadOfficeService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class RealTimeTransactionStart {
    private final EftBatchPaymentDetailRepository repository;
    private final RealTimeTransactionService service;
    private final BankHeadOfficeService ho;

    private final ExecutorService executorService;
    @Getter
    private boolean started = false;

    public RealTimeTransactionStart(@Qualifier("realTime") ExecutorService executorService, EftBatchPaymentDetailRepository repository, RealTimeTransactionService service, BankHeadOfficeService ho) {
        this.repository = repository;
        this.service = service;
        this.ho = ho;
        this.executorService = executorService;
    }

    public void start() {
        while (true) {
            List<EftPaymentRequestDetailProjection> list = repository.findRealTimePending();
            if (list.isEmpty()) {
                started = false;
                break;
            }
            started = true;
            list.forEach(eftPaymentRequestDetailProjection -> {
                try {
                    repository.updateNchlStatusByInstructionId("BUILD", eftPaymentRequestDetailProjection.getInstructionId());
                    executorService.submit(() -> service.ipsDctTransaction(eftPaymentRequestDetailProjection, ho.getHeadOfficeId(eftPaymentRequestDetailProjection.getCreditorAgent())));
                } catch (Exception e) {
                    log.error("Real Time Transaction Start ERROR:{}", e.getMessage());
                }
            });
            try {
                Thread.sleep(200L * list.size());
            } catch (Exception ignored) {
            }
        }
    }
}
