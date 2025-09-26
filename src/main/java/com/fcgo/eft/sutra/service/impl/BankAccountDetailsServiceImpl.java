package com.fcgo.eft.sutra.service.impl;


import com.fcgo.eft.sutra.dto.req.TransactionId;
import com.fcgo.eft.sutra.dto.res.BankAccountDetailsRes;
import com.fcgo.eft.sutra.entity.oracle.BankAccountWhitelist;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.repository.oracle.BankAccountWhitelistRepository;
import com.fcgo.eft.sutra.service.BankAccountDetailsService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeStatusFromNchl;
import com.fcgo.eft.sutra.service.realtime.RealTimeStatusFromNchl;
import com.fcgo.eft.sutra.token.NchlOauthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankAccountDetailsServiceImpl implements BankAccountDetailsService {
    @Value("${nchl.npi.url}")
    private String url;
    @Value("${nchl.npi.username}")
    private String username;
    private final NchlOauthToken oauthToken;
    private final WebClient webClient;
    private final BankAccountWhitelistRepository whitelistRepository;
    private final AccountWhiteListSave accountWhiteListSave;
    private final RealTimeStatusFromNchl getRealTimeStatus;
    private final NonRealTimeStatusFromNchl nonRealTimeStatusFromNchl;

    @Override
    public List<BankAccountWhitelist> getBankAccountDetails() {
        return whitelistRepository.findAll();
    }

    @Override
    public Object getTransactionDetailByInstructionId(String instructionId) {
        TransactionId type = whitelistRepository.findNchlTransactionType(instructionId)
                .orElseThrow(() -> new CustomException("Invalid INSTRUCTION ID " + instructionId));
        if (type.getTransactionType().equals("OFFUS")) {
            String batchId = whitelistRepository.findBatchId(type.getPaymentId());
            return nonRealTimeStatusFromNchl.checkByBatchNonRealTime(batchId);
        } else {
            return getRealTimeStatus.getRealTimeByBatch(instructionId);
        }
    }

    @Override
    public Object updateTransactionDetailByInstructionIdRealTime(String instructionId) {
        return getRealTimeStatus.getRealTimeByBatch(instructionId);

    }



    @Override
    public void fetchBankAccountDetails() {
        try {
            String access_token = oauthToken.getAccessToken();
            if (Objects.isNull(access_token)) {
                throw new RuntimeException("access_token is null: NCHL Connection issued.");
            }
            log.info("Fetching Bank Account Details: {}/api/bank-account/details", url);
            Objects.requireNonNull(webClient
                            .post()
                            .uri(url + "/api/bank-account/details")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + access_token)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(CustomException::new))
                            .bodyToMono(BankAccountDetailsRes.class)
                            .block())
                    .getData()
                    .forEach(d -> {
                        log.info("{} {} {}", d.getAccountId(), d.getBankId(), d.getAccountName());
                        try {
                            accountWhiteListSave.save(d.getAccountId(), username, d.getBankId(), d.getBranchId(), d.getAccountName(), d.getStatus(), d.getRcreTime(), d.getBankName());
                        } catch (Exception e) {
                            log.info("Account No:{} Bank:{} Save Error.", d.getAccountId(), d.getBankId());
                        }
                    });
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
