package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.mssql.NCHLWhiteList;
import com.fcgo.eft.sutra.entity.BankAccountWhitelist;
import com.fcgo.eft.sutra.entity.BankAccountWhitelistPk;
import com.fcgo.eft.sutra.repository.mssql.NCHLWhiteListRepository;
import com.fcgo.eft.sutra.repository.BankAccountWhitelistRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AccountWhiteListSave {
    private final BankAccountWhitelistRepository whitelistRepository;
    private final NCHLWhiteListRepository whiteListSuTRARepository;
    @Setter
    private long setDate = 0;


    public void save(String accountNo, String userId, String bankId, String branchId, String accountName, String status, String rcreTime, String bankName) {
        NCHLWhiteList mssql = whiteListSuTRARepository.findByBankIdAndAccountNumber(bankId, accountNo)
                .orElse(NCHLWhiteList.builder()
                        .id(whiteListSuTRARepository.findMaxId())
                        .userId(userId)
                        .bankId(bankId)
                        .branchId(branchId)
                        .accountNumber(accountNo)
                        .accountHolderName("")
                        .accountHolderId("")
                        .adminId(0)
                        .orgId(0)
                        .build());
        mssql.setAccountName(accountName);
        mssql.setStatus(status);
        whiteListSuTRARepository.save(mssql);

        whitelistRepository.save(BankAccountWhitelist
                .builder().pk(BankAccountWhitelistPk.builder().accountId(accountNo).bankId(bankId).build())
                .accountName(accountName)
                .bankName(bankName)
                .branchId(branchId)
                .rcreTime(rcreTime)
                .status(status)
                .updatedAt(setDate)
                .build());
        log.info("Account No:{} Bank:{} Save Success.", accountNo, bankId);
    }
}
