package com.fcgo.eft.sutra.service;

import com.fcgo.eft.sutra.entity.oracle.BankHeadOffice;
import com.fcgo.eft.sutra.repository.oracle.BankHeadOfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BankHeadOfficeService {
    private final BankHeadOfficeRepository repository;
    private final Map<String, String> bankHeadOffice = new HashMap<>();

    public void setHeadOfficeId() {
        for (BankHeadOffice headOffice : repository.findAll()) {
            bankHeadOffice.put(headOffice.getBankId(), headOffice.getHeadOfficeId());
        }
    }


    public String getHeadOfficeId(String bankId) {
        return bankHeadOffice.get(bankId);
    }


}
