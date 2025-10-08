package com.fcgo.eft.sutra.exception;

import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.RemoteIpRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunPost {
    private final RemoteIpRepository remoteIpRepository;
    private final AccEpaymentRepository epaymentRepository;
    @PostConstruct
    public void executePostConstruct() {

        remoteIpRepository.findByPendingTransactionId().forEach(ins -> {
            System.out.println( epaymentRepository.findByEftNo(Long.parseLong(ins)));
        });

    }
}
