package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class NchlReconciledService {
    private final NchlReconciledRepository repository;

    public NchlReconciled save(long instructionId, String debitResponseCode, String debitResponseMessage, String creditStatus, String reasonDesc, String transactionId, Date recDate) {
        return repository.save(NchlReconciled.builder()
                .instructionId(instructionId)
                .debitStatus(debitResponseCode)
                .debitMessage(debitResponseMessage)
                .creditStatus(creditStatus)
                .creditMessage(reasonDesc)
                .recDate(recDate)
                .transactionId(transactionId)
                .pushed("N")
                .build());
    }

    public void updateForResend(String instructionId) {
        repository.updateForResend(instructionId);
    }

    public void updateStatus(String instructionId) {
        repository.updateStatus(instructionId);
    }
}
