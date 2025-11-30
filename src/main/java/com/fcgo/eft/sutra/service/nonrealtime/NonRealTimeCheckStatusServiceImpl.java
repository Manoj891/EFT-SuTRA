package com.fcgo.eft.sutra.service.nonrealtime;

import com.fcgo.eft.sutra.dto.nchlres.NonRealTimeBatch;
import com.fcgo.eft.sutra.dto.res.NchlIpsBatchDetailRes;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.service.ReconciledTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = RuntimeException.class)
public class NonRealTimeCheckStatusServiceImpl implements NonRealTimeCheckStatusService {
    private final NchlReconciledRepository repository;
    private final ReconciledTransactionService reconciledTransactionService;
    private final NonRealTimeStatusFromNchl statusFromNchl;

    @Override
    public void checkStatusByDate(String date) {
        long time = new Date().getTime();
        statusFromNchl.checkStatusByDate(date)
                .forEach(response -> reconciledTransactionService.save(response.getNchlIpsBatchDetail(), response.getNchlIpsTransactionDetailList(), time));
    }

    @Override
    public void checkStatusByBatchId(String batchId) {
        long time = new Date().getTime();
        NonRealTimeBatch batch = statusFromNchl.checkByBatchNonRealTime(batchId);
        if (batch == null) {
            log.info("Batch id not found {}", batchId);
            return;
        }
        log.info("Check status by batch id {} found.", batchId);

        batch.getNchlIpsTransactionDetailList()
                .forEach(detail -> repository.save(NchlReconciled.builder()
                        .instructionId(detail.getInstructionId())
                        .debitStatus(batch.getDebitStatus())
                        .debitMessage(batch.getDebitReasonDesc())
                        .creditStatus(detail.getCreditStatus())
                        .creditMessage(detail.getReasonDesc())
                        .recDate(detail.getRecDate())
                        .transactionId(batch.getId() + "")
                        .pushed("N")
                        .build()));
    }


}
