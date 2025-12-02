package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.NchlReconciled;
import com.fcgo.eft.sutra.repository.NchlReconciledRepository;
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
        if (debitResponseMessage == null || debitResponseMessage.isEmpty()) {
            debitResponseMessage = "NA";
        }
        if (reasonDesc == null || reasonDesc.isEmpty()) {
            reasonDesc = "NA";
        }
        if (creditStatus == null || creditStatus.isEmpty()) {
            creditStatus = "NA";
        }
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
}
