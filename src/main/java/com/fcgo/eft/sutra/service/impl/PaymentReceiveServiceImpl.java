package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.BankMap;
import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.req.PaymentRequestNew;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.dto.res.PaymentSaved;
import com.fcgo.eft.sutra.entity.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.exception.PermissionDeniedException;
import com.fcgo.eft.sutra.security.AuthenticatedUser;
import com.fcgo.eft.sutra.security.AuthenticationFacade;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import com.fcgo.eft.sutra.service.PaymentSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
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
        statusUpdate(saved.getDetails());
        return PaymentReceiveStatus.builder().offus(saved.getOffus()).onus(saved.getOnus()).build();
    }

    @Override
    public PaymentReceiveStatus paymentReceive(PaymentRequestNew receive) {

        AuthenticatedUser user = facade.getAuthentication();
        if (!(user.getPaymentUser().equals("Y") && user.getAppName().equals("SuTRA")))
            throw new PermissionDeniedException();
        long poCode = receive.getPoCode();
        waitResourcesBusy(poCode);
        PaymentSaved saved = service.save(receive, user);
        service.busy(poCode, false);
        log.info("Commited. BATCH ID:{} {} ITEM RECEIVED", receive.getBatchId(), saved.getDetails().size());
        statusUpdate(saved.getDetails());
        return PaymentReceiveStatus.builder().offus(saved.getOffus()).onus(saved.getOnus()).build();

    }

    private void statusUpdate(List<EftBatchPaymentDetail> details) {
        executor.submit(() -> {
            Connection connection = null;
            Statement st = null;
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection("jdbc:sqlserver://10.100.199.148:1433;databaseName=SuTRA5;encrypt=false", "SuTRA_FCGO_LLG", "caPsKJSkD2-k38lEG4K");
                st = connection.createStatement();
                connection.setAutoCommit(true);
                for (EftBatchPaymentDetail detail : details) {
                    st.executeUpdate("update acc_epayment set transtatus=2,pstatus=2,paymentdate=GETDATE() where eftno=" + detail.getInstructionId());
                }
            } catch (Exception ignored) {
            }
            try {
                assert connection != null;
                connection.close();
                assert st != null;
                st.close();
            } catch (Exception ignored) {
            }
        });

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
