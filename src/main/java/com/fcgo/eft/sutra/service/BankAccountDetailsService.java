package com.fcgo.eft.sutra.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fcgo.eft.sutra.dto.nchlres.RealTimeBatch;
import com.fcgo.eft.sutra.entity.oracle.BankAccountWhitelist;

import java.util.List;

public interface BankAccountDetailsService {
    void fetchBankAccountDetails();

    List<BankAccountWhitelist> getBankAccountDetails();

    Object getTransactionDetailByInstructionId(String instructionId);

    void updateTransactionDetailByInstructionIdRealTime(String instructionId);
}
