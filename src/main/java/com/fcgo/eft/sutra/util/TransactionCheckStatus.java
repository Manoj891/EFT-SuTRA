package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.dto.nchlres.NonRealTimeBatch;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.repository.oracle.BankHeadOfficeRepository;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.service.BankAccountDetailsService;
import com.fcgo.eft.sutra.service.BankHeadOfficeService;
import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeCheckStatusByDate;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeStatusFromNchl;
import com.fcgo.eft.sutra.service.realtime.RealTimeStatusFromNchl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCheckStatus {

    private final NonRealTimeCheckStatusByDate nonRealTime;
    private final RealTimeStatusFromNchl realTime;
    private final NchlReconciledRepository repository;
    private final TransactionStatusUpdate statusUpdate;
    private final BankAccountDetailsService bankAccountDetailsService;
    private final BankHeadOfficeService bankHeadOfficeService;
    private final BankHeadOfficeRepository headOfficeRepository;
    private final PaymentReceiveService bankMapService;
    private final EftPaymentReceiveService paymentReceiveService;

private final NonRealTimeStatusFromNchl nonRealTimeStatusFromNchl;

    @PostConstruct
    public void executePostConstruct() {
        bankHeadOfficeService.setHeadOfficeId();
        bankMapService.setBankMaps(headOfficeRepository.findBankMap());
//        repository.findByPushed("N").forEach(statusUpdate::update);
        NonRealTimeBatch nonRealTimeBatch= nonRealTimeStatusFromNchl.checkByBatchNonRealTime("SU7701522532159151");
        System.out.println(nonRealTimeBatch.getDebitStatus());
        System.out.println(nonRealTimeBatch.getDebitReasonCode());
        nonRealTimeBatch.getNchlIpsTransactionDetailList().forEach(nonRealTimeBatchDetail -> {
            System.out.println(nonRealTimeBatchDetail.getCreditStatus()+" "+nonRealTimeBatchDetail.getInstructionId());
        });

//        System.out.println("----------------------------------------------------------------------------------");
//        bankAccountDetailsService.updateTransactionDetailByInstructionIdRealTime("81040382830000404");
//        System.out.println("----------------------------------------------------------------------------------");
//        System.out.println(bankAccountDetailsService.getTransactionDetailByInstructionId("83350682830000952"));
//        System.out.println("----------------------------------------------------------------------------------");
//        System.out.println(bankAccountDetailsService.getTransactionDetailByInstructionId("83350682830000954"));
//        System.out.println("----------------------------------------------------------------------------------");
//        System.out.println(bankAccountDetailsService.getTransactionDetailByInstructionId("83350682830000886"));
//        System.out.println("----------------------------------------------------------------------------------");
//        headOfficeRepository.updatePaymentPendingStatusDetail();
//        headOfficeRepository.updatePaymentPendingStatusMaster();
//        new Thread(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build())).start();

    }

    //    @Scheduled(cron = "0 10 10,12,14,15,16,17,18 * * *")
    public void executeCheckTransactionStatus() {
        headOfficeRepository.updatePaymentPendingStatusDetail();
        headOfficeRepository.updatePaymentPendingStatusMaster();
        repository.findByPendingDate().forEach(date -> {
            nonRealTime.nonRealtimeCheckUpdate(date);
            realTime.realTimeCheckByDate(date);
        });
        repository.findByPushed("N").forEach(statusUpdate::update);
    }

    //    @Scheduled(cron = "0 0 10,16,20 * * *")
    public void fetchBankAccountDetails() {
        bankAccountDetailsService.fetchBankAccountDetails();
    }
}
