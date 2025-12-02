package com.fcgo.eft.sutra.service.impl;


import com.fcgo.eft.sutra.dto.res.BankAccountWhitelistPushed;
import com.fcgo.eft.sutra.entity.BankAccountWhitelist;
import com.fcgo.eft.sutra.entity.BankAccountWhitelistPk;
import com.fcgo.eft.sutra.repository.BankAccountWhitelistRepository;
import com.fcgo.eft.sutra.util.TransactionStatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AccountWhiteListSave {
    private final BankAccountWhitelistRepository whitelistRepository;
    private final TransactionStatusUpdate update;
    private final WebClient webClient;
    @Setter
    private long setDate = 0;
    @Value("${nchl.npi.username}")
    private String username;


    public void save(String accountNo, String bankId, String branchId, String accountName, String status, String rcreTime, String bankName) {
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

    public void pushInSuTRA() {
        String token = update.getToken();
        if (token == null) {
            update.init();
            token = update.getToken();
        }
        Pageable limit = PageRequest.of(0, 1000);
        for (int i = 1; i < 100; i++) {
            List<BankAccountWhitelistPushed> list = whitelistRepository.findByPushedOrPushedNull("N", limit);
            if (list.isEmpty()) {
                break;
            }
            List<String> res = webClient.post()
                    .uri("https://sutrav3.fcgo.gov.np/SuTRAv3/utility/bank-account-whitelist?username=" + username)
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(list)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.info("Remote error: {}", errorBody);
                                        return Mono.error(new RuntimeException("Remote API returned error"));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                    })
                    .block();
            assert res != null;
            res.forEach(d -> {
                try {
                    String[] acc = d.split("-");
                    log.info("{} {}.", acc[0], acc[1]);
                    whitelistRepository.updateStatus(acc[0], acc[1]);
                } catch (Exception ex) {
                    log.info(ex.getMessage());
                }
            });
        }
    }
}
