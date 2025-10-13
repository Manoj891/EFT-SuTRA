package com.fcgo.eft.sutra.service.nonrealtime;

import com.fcgo.eft.sutra.dto.req.CipsFundTransfer;
import com.fcgo.eft.sutra.dto.req.NchlIpsBatchDetail;
import com.fcgo.eft.sutra.dto.req.NchlIpsTransactionDetailList;
import com.fcgo.eft.sutra.dto.res.PaymentBatchPendingRes;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.service.BankHeadOfficeService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class NonRealTimeTransactionStart {
    @Getter
    private boolean started = false;
    private final ThreadPoolExecutor executor;
    private final EftBatchPaymentDetailRepository repository;
    private final BatchPaymentService service;
    private final BankHeadOfficeService ho;

    public NonRealTimeTransactionStart(@Qualifier("nonRealTime") ThreadPoolExecutor executor, EftBatchPaymentDetailRepository repository, BatchPaymentService batch, BankHeadOfficeService ho) {
        this.executor = executor;
        this.repository = repository;
        this.service = batch;
        this.ho = ho;
    }

    public void start() {
        while (true) {
            List<PaymentBatchPendingRes> list = repository.findPaymentNonRealPendingRes();
            if (list.isEmpty()) {
                log.info("Non Real Time new record not found");
                started = false;
                break;
            }
            started = true;
            list.forEach(batch -> {
                try {
                    BigInteger id = batch.getId();
                    String agent = batch.getAgent();
                    String account = batch.getAccount();
                    String branch = batch.getBranch();
                    String name = batch.getName();
                    String purpose = batch.getPurpose();
                    String batchId = batch.getBatchId();

                    NchlIpsBatchDetail batchDetail = NchlIpsBatchDetail.builder().categoryPurpose(purpose).debtorAgent(agent).debtorBranch(branch).debtorName(name).debtorAccount(account).batchId(batchId).batchCount(batch.getOffus()).batchCrncy("NPR").build();
                    List<NchlIpsTransactionDetailList> data = new ArrayList<>();

                    repository.findByEftBatchPaymentIdAndNchlTransactionTypeAndNchlCreditStatusNull(id, "OFFUS")
                            .forEach(d ->
                                    data.add(NchlIpsTransactionDetailList.builder()
                                            .instructionId(d.getInstructionId())
                                            .endToEndId(d.getEndToEndId())
                                            .amount(d.getAmount())
                                            .purpose(purpose)
                                            .creditorAgent(d.getCreditorAgent())
                                            .creditorBranch(ho.getHeadOfficeId(d.getCreditorAgent()))
                                            .creditorName(d.getCreditorName())
                                            .creditorAccount(d.getCreditorAccount())
                                            .addenda1(d.getAddenda1())
                                            .addenda2(d.getAddenda2())
                                            .addenda3(d.getAddenda3())
                                            .addenda4(d.getAddenda4())
                                            .refId(d.getRefId())
                                            .remarks(d.getRemarks())
                                            .build()));
                    batchDetail.setBatchAmount(data.stream().map(NchlIpsTransactionDetailList::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
                    batchDetail.setBatchCount(data.size());
                    CipsFundTransfer transfer = CipsFundTransfer.builder().nchlIpsBatchDetail(batchDetail).nchlIpsTransactionDetailList(data).build();
                    repository.updateBatchBuild("BUILD", id);
                    repository.updateBatchBuild(id);
                    executor.execute(() -> service.start(transfer, id));
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            });


            int activeThread = executor.getActiveCount();
            while (activeThread > 10) {
                try {
                    log.info("Non Realtime waiting for clearing pool. Active threads: {}", activeThread);
                    int sleep;
                    if (activeThread > 40) {
                        sleep = 10000;
                    } else if (activeThread > 30) {
                        sleep = 5000;
                    } else if (activeThread > 20) {
                        sleep = 3000;
                    } else {
                        sleep = 2000;
                    }
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
