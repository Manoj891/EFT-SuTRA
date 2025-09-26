package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.AccountBalanceReq;
import com.fcgo.eft.sutra.dto.req.ValidateBankAccountReq;
import com.fcgo.eft.sutra.dto.res.AccountBalanceRes;
import com.fcgo.eft.sutra.dto.res.ValidateBankAccountRes;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.service.AccountValidationService;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountValidationServiceImpl implements AccountValidationService {
    @Value("${nchl.npi.url}")
    private String url;
    private final NchlOauthToken oauthToken;
    private final WebClient webClient;
    private final PaymentReceiveService receiveService;

    @Override
    public ValidateBankAccountRes accountValidation(ValidateBankAccountReq req) {
        try {
            String bankId = receiveService.getBankMap().get(req.getBankId());
            if (Objects.isNull(bankId)) {
                throw new CustomException("Bank id not found");
            }
            return webClient
                    .post()
                    .uri(url + "/api/validatebankaccount")
                    .header("Authorization", "Bearer " + oauthToken.getAccessToken())
                    .header("Content-Type", "application/json")
                    .bodyValue("{\"bankId\":\"" + bankId + "\",\"accountId\" : \"" + req.getAccountId() + "\",\"accountName\":\"" + req.getAccountName() + "\"}")
                    .retrieve()
                    .bodyToMono(ValidateBankAccountRes.class)
                    .block();
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public AccountBalanceRes accountBalance(AccountBalanceReq req) {
        try {
            String bankId = receiveService.getBankMap().get(req.getBankId());
            if (Objects.isNull(bankId)) {
                throw new CustomException("Bank id not found");
            }
            return webClient
                    .post()
                    .uri(url + "/api/account/balance")
                    .header("Authorization", "Bearer " + oauthToken.getAccessToken())
                    .header("Content-Type", "application/json")
                    .bodyValue("{\"bankId\":\"" + bankId + "\",\"accountId\" : \"" + req.getAccountId() + "\",\"branchId\":\"" + req.getAccountId() + "\"}")
                    .retrieve()
                    .bodyToMono(AccountBalanceRes.class)
                    .block();
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }


}
