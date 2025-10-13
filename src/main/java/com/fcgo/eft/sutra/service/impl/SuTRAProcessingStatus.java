package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.util.TransactionStatusUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = CustomException.class)
public class SuTRAProcessingStatus {
    private final NchlReconciledRepository reconciledRepository;
    private final TransactionStatusUpdate statusUpdate;
    private final EftBatchPaymentDetailRepository detailRepository;
    private final AccEpaymentRepository epaymentRepository;
    public void check(long eftNo){
        Optional<NchlReconciled> reconciled = reconciledRepository.findByInstructionId(eftNo);
        if (reconciled.isPresent()) {
            statusUpdate.update(reconciled.get());
        } else {
            String instructionId = String.valueOf(eftNo);
            Optional<EftBatchPaymentDetail> batchPaymentDetail =
                    detailRepository.findByInstructionId(instructionId);
            if (batchPaymentDetail.isEmpty()) {
                epaymentRepository.updateRevertInSuTra(eftNo);
            }
        }
    }
}
