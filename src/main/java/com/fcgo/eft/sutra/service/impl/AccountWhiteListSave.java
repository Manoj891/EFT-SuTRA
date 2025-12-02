package com.fcgo.eft.sutra.service.impl;


import com.fcgo.eft.sutra.entity.BankAccountWhitelist;
import com.fcgo.eft.sutra.entity.BankAccountWhitelistPk;
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
    @Setter
    private long setDate = 0;


    public void save(String accountNo, String userId, String bankId, String branchId, String accountName, String status, String rcreTime, String bankName) {


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
