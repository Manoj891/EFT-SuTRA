package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.util.DbPrimary;
import com.fcgo.eft.sutra.util.TransactionStatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuTRAProcessingStatus {
    private final NchlReconciledRepository reconciledRepository;
    private final TransactionStatusUpdate statusUpdate;
    private final EftBatchPaymentDetailRepository detailRepository;
    private final DbPrimary dbPrimary;

    public void check(long eftNo) {
        try {
            Optional<NchlReconciled> reconciled = reconciledRepository.findByInstructionId(eftNo);
            if (reconciled.isPresent()) {
                statusUpdate.update(reconciled.get());
            } else {
                String instructionId = String.valueOf(eftNo);
                Optional<EftBatchPaymentDetail> batchPaymentDetail = detailRepository.findByInstructionId(instructionId);
                if (batchPaymentDetail.isPresent()) {
                    log.info("Transaction found in EftBatchPaymentDetail: {}", instructionId);
                } else {
                    dbPrimary.update("update acc_epayment set transtatus=1,pstatus=0 where eftno=" + eftNo);
                    log.info("EFT No. {} has been reverted to sutra", eftNo);
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
