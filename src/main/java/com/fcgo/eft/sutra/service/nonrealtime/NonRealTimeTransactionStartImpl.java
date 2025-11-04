package com.fcgo.eft.sutra.service.nonrealtime;

import com.fcgo.eft.sutra.dto.req.CipsFundTransfer;
import com.fcgo.eft.sutra.dto.req.NchlIpsBatchDetail;
import com.fcgo.eft.sutra.dto.req.NchlIpsTransactionDetailList;
import com.fcgo.eft.sutra.dto.res.PaymentBatchPendingRes;
import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.service.BankHeadOfficeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class NonRealTimeTransactionStartImpl implements NonRealTimeTransactionStart {


    private boolean started = false;
    private final ThreadPoolExecutor executor;
    private final EftBatchPaymentDetailRepository repository;
    private final BatchPaymentService service;
    private final BankHeadOfficeService ho;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public NonRealTimeTransactionStartImpl(@Qualifier("nonRealTime") ThreadPoolExecutor executor, EftBatchPaymentDetailRepository repository, BatchPaymentService batch, BankHeadOfficeService ho) {
        this.executor = executor;
        this.repository = repository;
        this.service = batch;
        this.ho = ho;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void start() {
        while (true) {
            List<PaymentBatchPendingRes> list = repository.findPaymentNonRealPendingRes();
            if (list.isEmpty()) {
                started = false;
                break;
            }
            started = true;
            long dateTime = Long.parseLong(sdf.format(new Date()));
            list.forEach(batch -> {
                try {
                    BigInteger id = batch.getId();
                    String agent = batch.getAgent();
                    String account = batch.getAccount();
                    String branch = batch.getBranch();
                    String name = batch.getName();
                    String purpose = batch.getPurpose();
                    String batchId = batch.getBatchId();
                    int batchCount = batch.getOffus();

                    NchlIpsBatchDetail batchDetail = NchlIpsBatchDetail.builder().categoryPurpose(purpose).debtorAgent(agent).debtorBranch(branch).debtorName(name).debtorAccount(account).batchId(batchId).batchCount(batch.getOffus()).batchCrncy("NPR").build();
                    List<NchlIpsTransactionDetailList> data = new ArrayList<>();

                    List<EftBatchPaymentDetail> details = repository.findByEftBatchPaymentIdAndNchlTransactionTypeAndNchlCreditStatusNullAndNchlPushedDateTimeNull(id, "OFFUS");
                    if (details.size() == batchCount) {
                        details.forEach(d ->
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
                        batchDetail.setBatchCount(batchCount);
                        CipsFundTransfer transfer = CipsFundTransfer.builder().nchlIpsBatchDetail(batchDetail).nchlIpsTransactionDetailList(data).build();
                        repository.updateBatchBuild("BUILD", dateTime, id);
                        repository.updateBatchBuild(id);
                        executor.execute(() -> service.start(transfer, id));
                    } else {
                        repository.updateBatchBuild(id);
                        log.info("Invalid Batch count {} but actual size is {}", batchCount, details.size());
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            });


            int activeThread = executor.getActiveCount();
            while (activeThread > 5) {
                int sleep;
                if (activeThread > 35) {
                    sleep = 30000;
                } else if (activeThread > 25) {
                    sleep = 25000;
                } else if (activeThread > 15) {
                    sleep = 15000;
                } else if (activeThread > 12) {
                    sleep = 12000;
                } else if (activeThread > 10) {
                    sleep = 10000;
                } else {
                    sleep = 5000;
                }
                try {
                    log.info("Realtime waiting for clearing pool. Active threads: {} {} Second.", activeThread, (sleep / 1000));
                    Thread.sleep(sleep);
                } catch (InterruptedException ignored) {
                }
                activeThread = executor.getActiveCount();
            }
        }
    }
}
