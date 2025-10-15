package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.BankMap;
import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.exception.PermissionDeniedException;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.security.AuthenticatedUser;
import com.fcgo.eft.sutra.security.AuthenticationFacade;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentReceiveServiceImpl implements PaymentReceiveService {
    private final AuthenticationFacade facade;
    private final PaymentSaveService service;
    private final AccEpaymentRepository epaymentRepository;


    @Override
    public void setBankMaps(List<BankMap> bankMaps) {
        service.setBankMaps(bankMaps);
    }

    @Override
    public Map<String, String> getBankMap() {
        return service.getBankMap();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public PaymentReceiveStatus paymentReceive(EftPaymentReceive receive) {
        AuthenticatedUser user = facade.getAuthentication();
        if (!(user.getPaymentUser().equals("Y") && user.getAppName().equals("SuTRA")))
            throw new PermissionDeniedException();
        long poCode = receive.getPaymentRequest().getPoCode();
        waitResourcesBusy(poCode);
        List<EftBatchPaymentDetail> list = service.save(receive, user);
        service.busy(poCode, false);
        log.info("Commited. BATCH ID:{} {} ITEM RECEIVED", receive.getPaymentRequest().getBatchId(), list.size());
        int offus = 0, onus = 0;
        for (EftBatchPaymentDetail detail : list) {
            epaymentRepository.updateStatusProcessing(Long.parseLong(detail.getInstructionId()));
            if (detail.getNchlTransactionType().equals("OFFUS")) {
                offus++;
            } else {
                onus++;
            }
        }
        return PaymentReceiveStatus.builder().offus(offus).onus(onus).build();
    }

    private void waitResourcesBusy(long poCode) {
        try {
            Boolean status = service.getStatus().get(poCode);
            if (status != null) {
                while (status) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    status = service.getStatus().get(poCode);
                }
            }
        } catch (Exception ignored) {
        }
        service.busy(poCode, true);
    }
}
