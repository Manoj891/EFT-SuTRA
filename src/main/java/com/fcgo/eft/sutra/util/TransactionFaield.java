package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionFaield {
    private final NchlReconciledRepository reconciledRepository;

    public void faield(String instructionId) {
        reconciledRepository.insertNchlReconciled(instructionId);
        reconciledRepository.updateNchlReconciled(instructionId);
        System.out.println(instructionId + " failed to reconcile");
    }
}
