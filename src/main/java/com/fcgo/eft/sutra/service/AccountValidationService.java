package com.fcgo.eft.sutra.service;


import com.fcgo.eft.sutra.dto.req.AccountBalanceReq;
import com.fcgo.eft.sutra.dto.req.ValidateBankAccountReq;
import com.fcgo.eft.sutra.dto.res.AccountBalanceRes;
import com.fcgo.eft.sutra.dto.res.ValidateBankAccountRes;


public interface AccountValidationService {
    ValidateBankAccountRes accountValidation(ValidateBankAccountReq req);

    AccountBalanceRes accountBalance(AccountBalanceReq req);

}
