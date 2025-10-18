package com.fcgo.eft.sutra.service.nonrealtime;

import com.fcgo.eft.sutra.dto.nchlres.NonRealTimeBatch;
import com.fcgo.eft.sutra.dto.res.NchlIpsBatchDetailRes;
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
        reconciledTransactionService.save(NchlIpsBatchDetailRes.builder()
                .id(batch.getId())
                .batchId(batch.getBatchId())
                .recDate(batch.getRecDate())
                .isoTxnId(batch.getIsoTxnId())
                .batchAmount(batch.getBatchAmount())
                .batchCount(batch.getBatchCount())
                .batchCrncy(batch.getBatchCrncy())
                .categoryPurpose(batch.getCategoryPurpose())
                .debtorAgent(batch.getDebtorAgent())
                .debtorBranch(batch.getDebtorBranch())
                .debtorName(batch.getDebtorName())
                .debtorAccount(batch.getDebtorAccount())
                .debitStatus(batch.getDebitStatus())
                .rcreTime(batch.getRcreTime())
                .debitReasonDesc(batch.getDebitReasonDesc())
                .build(), batch.getNchlIpsTransactionDetailList(), time);

    }

}
