package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.oracle.ReconciledTransaction;
import com.fcgo.eft.sutra.entity.oracle.ReconciledTransactionDetail;
import com.fcgo.eft.sutra.repository.oracle.ReconciledTransactionDetailRepository;
import com.fcgo.eft.sutra.repository.oracle.ReconciledTransactionRepository;
import com.fcgo.eft.sutra.service.ReconciledTransactionService;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransaction;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransactionDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ReconciledTransactionServiceImpl implements ReconciledTransactionService {
    private final NchlReconciledService repository;
    private final ReconciledTransactionRepository transactionRepository;
    private final ReconciledTransactionDetailRepository detailRepository;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void save(RealTimeTransaction rtt, RealTimeTransactionDetail detail, long time) {
        String id = "ONOS-" + detail.getId();
        transactionRepository.save(ReconciledTransaction.builder()
                .entityId(id)
                .id(detail.getId())
                .batchId(String.valueOf(detail.getInstructionId()))
                .recDate(sdf.format(detail.getRecDate()))
                .batchCrncy("NPR")
                .categoryPurpose(rtt.getCategoryPurpose())
                .debtorAgent(rtt.getDebtorAgent())
                .debtorBranch(rtt.getDebtorBranch())
                .debtorName(rtt.getDebtorName())
                .debtorAccount(rtt.getDebtorAccount())
                .debitStatus(rtt.getDebitStatus())
                .debitReasonDesc(rtt.getDebitReasonDesc())
                .settlementDate(rtt.getRecDate())
                .txnResponse(rtt.getDebitReasonDesc())
                .createdAt(time)
                .updatedAt(time)
                .build());
        detailRepository.save(ReconciledTransactionDetail.builder().entityId(id + "-" + detail.getId()).id(String.valueOf(detail.getId())).recDate(detail.getRecDate()).instructionId(detail.getInstructionId()).endToEndId(detail.getEndToEndId()).chargeLiability(detail.getChargeLiability()).purpose(detail.getPurpose()).creditStatus(detail.getCreditStatus()).reasonCode(detail.getCreditStatus()).remarks(detail.getRemarks()).particulars(detail.getReasonDesc()).reasonDesc(detail.getReasonDesc()).amount(detail.getAmount()).chargeAmount(detail.getChargeAmount()).creditorAgent(detail.getCreditorAgent()).creditorBranch(detail.getCreditorBranch()).creditorName(detail.getCreditorName()).creditorAccount(detail.getCreditorAccount()).addenda1(detail.getAddenda1()).addenda2(detail.getAddenda2()).addenda3(detail.getAddenda3()).addenda4(detail.getAddenda4()).refId(detail.getRefId()).reconciledTransactionId(id).build());
        repository.save(detail.getInstructionId(), rtt.getDebitStatus(), rtt.getDebitReasonDesc(), detail.getCreditStatus(), detail.getReasonDesc(), detail.getInstructionId() + "", detail.getRecDate());
        log.info("InstructionId: {} status: {} {}", detail.getInstructionId(), detail.getCreditStatus(), detail.getReasonDesc());
    }
}
