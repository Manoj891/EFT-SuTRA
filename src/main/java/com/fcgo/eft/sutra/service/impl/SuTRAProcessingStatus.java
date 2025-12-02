package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.entity.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.entity.NchlReconciled;
import com.fcgo.eft.sutra.repository.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.repository.NchlReconciledRepository;
import com.fcgo.eft.sutra.util.TransactionStatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuTRAProcessingStatus {
    private final NchlReconciledRepository reconciledRepository;
    private final TransactionStatusUpdate statusUpdate;
    private final EftBatchPaymentDetailRepository detailRepository;
    private final StringToJsonNode jsonNode;

    public void check(long eftNo) {
        try {
            long datetime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date()));
            Optional<NchlReconciled> reconciled = reconciledRepository.findByInstructionId(eftNo);
            if (reconciled.isPresent()) {
                statusUpdate.update(reconciled.get(), datetime);
            } else {
                String instructionId = String.valueOf(eftNo);
                Optional<EftBatchPaymentDetail> batchPaymentDetail = detailRepository.findByInstructionId(instructionId);
                if (batchPaymentDetail.isPresent()) {
                    log.info("Transaction found in EftBatchPaymentDetail: {}", instructionId);
                } else {
//                    dbPrimary.update("update acc_epayment set transtatus=1,pstatus=0 where eftno=" + eftNo);
                    log.info("EFT No. {} has been reverted to sutra", eftNo);
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
