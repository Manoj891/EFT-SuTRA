package com.fcgo.eft.sutra.service;

import com.fcgo.eft.sutra.entity.oracle.BankAccountWhitelist;

import java.util.List;

public interface BankAccountDetailsService {
    void fetchBankAccountDetails();

    List<BankAccountWhitelist> getBankAccountDetails();

    Object getTransactionDetailByInstructionId(String instructionId);

    Object updateTransactionDetailByInstructionIdRealTime(String instructionId);
}
