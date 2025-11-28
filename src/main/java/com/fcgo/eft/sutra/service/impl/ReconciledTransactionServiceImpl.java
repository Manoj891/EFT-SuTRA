package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.nchlres.NonRealTimeBatch;
import com.fcgo.eft.sutra.dto.res.NchlIpsBatchDetailRes;
import com.fcgo.eft.sutra.dto.res.NchlIpsTransactionDetail;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.entity.oracle.ReconciledTransaction;
import com.fcgo.eft.sutra.entity.oracle.ReconciledTransactionDetail;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.repository.oracle.ReconciledTransactionDetailRepository;
import com.fcgo.eft.sutra.repository.oracle.ReconciledTransactionRepository;
import com.fcgo.eft.sutra.service.ReconciledTransactionService;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransaction;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransactionDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = RuntimeException.class)
public class ReconciledTransactionServiceImpl implements ReconciledTransactionService {
    private final ReconciledTransactionRepository transactionRepository;
    private final ReconciledTransactionDetailRepository detailRepository;
    private final NchlReconciledRepository repository;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void save(RealTimeTransaction rtt, RealTimeTransactionDetail detail, long time) {
//        String id = "ONOS-" + detail.getId();
//        transactionRepository.save(ReconciledTransaction.builder().entityId(id).id(detail.getId()).batchId(String.valueOf(detail.getInstructionId())).recDate(sdf.format(detail.getRecDate())).batchCrncy("NPR").categoryPurpose(rtt.getCategoryPurpose()).debtorAgent(rtt.getDebtorAgent()).debtorBranch(rtt.getDebtorBranch()).debtorName(rtt.getDebtorName()).debtorAccount(rtt.getDebtorAccount()).debitStatus(rtt.getDebitStatus()).debitReasonDesc(rtt.getDebitReasonDesc()).settlementDate(rtt.getRecDate()).txnResponse(rtt.getDebitReasonDesc()).createdAt(time).updatedAt(time).build());
//        detailRepository.save(ReconciledTransactionDetail.builder().entityId(id + "-" + detail.getId()).id(String.valueOf(detail.getId())).recDate(detail.getRecDate()).instructionId(detail.getInstructionId()).endToEndId(detail.getEndToEndId()).chargeLiability(detail.getChargeLiability()).purpose(detail.getPurpose()).creditStatus(detail.getCreditStatus()).reasonCode(detail.getCreditStatus()).remarks(detail.getRemarks()).particulars(detail.getReasonDesc()).reasonDesc(detail.getReasonDesc()).amount(detail.getAmount()).chargeAmount(detail.getChargeAmount()).creditorAgent(detail.getCreditorAgent()).creditorBranch(detail.getCreditorBranch()).creditorName(detail.getCreditorName()).creditorAccount(detail.getCreditorAccount()).addenda1(detail.getAddenda1()).addenda2(detail.getAddenda2()).addenda3(detail.getAddenda3()).addenda4(detail.getAddenda4()).refId(detail.getRefId()).reconciledTransactionId(id).build());
//

        repository.save(NchlReconciled.builder().instructionId(detail.getInstructionId()).debitStatus(rtt.getDebitStatus()).debitMessage(rtt.getDebitReasonDesc()).creditStatus(detail.getCreditStatus()).creditMessage(detail.getReasonDesc()).recDate(detail.getRecDate()).transactionId(detail.getInstructionId() + "").pushed("N").build());
        log.info("InstructionId: {} status: {} {}", detail.getInstructionId(), detail.getCreditStatus(), detail.getReasonDesc());
    }

    @Override
    public void save(NchlIpsBatchDetailRes batch, List<NchlIpsTransactionDetail> details, long time) {
        if (batch != null && batch.getDebitStatus() != null && batch.getDebitStatus().length() > 1) {
            String id = batch.getBatchId() + "-" + batch.getId();
            transactionRepository.save(ReconciledTransaction.builder().entityId(id).id(batch.getId()).batchId(batch.getBatchId()).recDate(batch.getRecDate()).batchCrncy(batch.getBatchCrncy()).categoryPurpose(batch.getCategoryPurpose()).debtorAgent(batch.getDebtorAgent()).debtorBranch(batch.getDebtorBranch()).debtorName(batch.getDebtorName()).debtorAccount(batch.getDebtorAccount()).debitStatus(batch.getDebitStatus()).debitReasonDesc(batch.getDebitReasonDesc()).settlementDate(batch.getRecDate()).txnResponse(batch.getDebitReasonDesc()).updatedAt(time).createdAt(time).build());
            details.forEach(detail -> {
                if (detail.getCreditStatus() != null && detail.getCreditStatus().length() > 1) {
                    try {
                        log.info("InstructionId: {} status: {}", detail.getInstructionId(), detail.getCreditStatus());
                        String transactionId = "";
                        try {
                            transactionId = detail.getId();
                            if (transactionId != null) {
                                transactionId = "" + detail.getInstructionId();
                            }
                        } catch (Exception ignored) {
                        }
                        detailRepository.save(ReconciledTransactionDetail.builder().entityId(id + "-" + detail.getId()).id(detail.getId()).recDate(detail.getRecDate()).instructionId(detail.getInstructionId()).endToEndId(detail.getEndToEndId()).chargeLiability(detail.getChargeLiability()).purpose(detail.getPurpose()).creditStatus(detail.getCreditStatus()).reasonCode(detail.getCreditStatus()).remarks(detail.getRemarks()).particulars(detail.getReasonDesc()).reasonDesc(detail.getReasonDesc()).amount(detail.getAmount()).chargeAmount(detail.getChargeAmount()).creditorAgent(detail.getCreditorAgent()).creditorBranch(detail.getCreditorBranch()).creditorName(detail.getCreditorName()).creditorAccount(detail.getCreditorAccount()).addenda1(detail.getAddenda1()).addenda2(detail.getAddenda2()).addenda3(detail.getAddenda3()).addenda4(detail.getAddenda4()).refId(detail.getRefId()).reconciledTransactionId(id).build());
                        repository.save(NchlReconciled.builder().instructionId(detail.getInstructionId()).debitStatus(batch.getDebitStatus()).debitMessage(batch.getDebitReasonDesc()).creditStatus(detail.getCreditStatus()).creditMessage(detail.getReasonDesc()).recDate(detail.getRecDate()).transactionId(transactionId).pushed("N").build());
                    } catch (Exception i) {
                        log.info(i.getMessage());
                    }
                }

            });
        }
    }

    @Override
    public void save(NonRealTimeBatch batch, long time) {
        save(NchlIpsBatchDetailRes.builder()
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
