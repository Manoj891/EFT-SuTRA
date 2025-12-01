package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.BankMap;
import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.req.PaymentRequestNew;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.dto.res.PaymentSaved;
import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.exception.PermissionDeniedException;
import com.fcgo.eft.sutra.security.AuthenticatedUser;
import com.fcgo.eft.sutra.security.AuthenticationFacade;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import com.fcgo.eft.sutra.service.PaymentSaveService;
import com.fcgo.eft.sutra.util.DbPrimary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentReceiveServiceImpl implements PaymentReceiveService {
    private final AuthenticationFacade facade;
    private final PaymentSaveService service;
    private final ThreadPoolExecutor executor;


    @Override
    public void setBankMaps(List<BankMap> bankMaps) {
        service.setBankMaps(bankMaps);
    }

    @Override
    public Map<String, String> getBankMap() {
        return service.getBankMap();
    }

    @Override
    public PaymentReceiveStatus paymentReceive(EftPaymentReceive receive) {
        AuthenticatedUser user = facade.getAuthentication();
        if (!(user.getPaymentUser().equals("Y") && user.getAppName().equals("SuTRA")))
            throw new PermissionDeniedException();
        long poCode = receive.getPaymentRequest().getPoCode();
        waitResourcesBusy(poCode);
             PaymentSaved saved = service.save(receive, user);
        service.busy(poCode, false);
        log.info("Commited. BATCH ID:{} {} ITEM RECEIVED", receive.getPaymentRequest().getBatchId(), saved.getDetails().size());

//        executor.submit(() -> {
//            for (EftBatchPaymentDetail detail : saved.getDetails()) {
//                epaymentRepository.updateStatusProcessing(detail.getInstructionId());
//            }
//            epaymentRepository.closeStatusUpdateProcessing();
//        });

        return PaymentReceiveStatus.builder().offus(saved.getOffus()).onus(saved.getOnus()).build();
    }

    @Override
    public PaymentReceiveStatus paymentReceive(PaymentRequestNew receive) {

        AuthenticatedUser user = facade.getAuthentication();
        if (!(user.getPaymentUser().equals("Y") && user.getAppName().equals("SuTRA")))
            throw new PermissionDeniedException();
        long poCode = receive.getPoCode();
        waitResourcesBusy(poCode);
//        epaymentRepository.initStatusUpdateProcessing();
        PaymentSaved saved = service.save(receive, user);
        service.busy(poCode, false);
        log.info("Commited. BATCH ID:{} {} ITEM RECEIVED", receive.getBatchId(), saved.getDetails().size());

        return PaymentReceiveStatus.builder().offus(saved.getOffus()).onus(saved.getOnus()).build();

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
