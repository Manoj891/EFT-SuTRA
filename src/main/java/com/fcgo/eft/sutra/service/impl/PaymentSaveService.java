package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.entity.oracle.*;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.BankAccountWhitelistRepository;
import com.fcgo.eft.sutra.repository.oracle.BankHeadOfficeRepository;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.repository.oracle.EftPaymentRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PaymentSaveService {
    private final EftPaymentRequestRepository repository;
    private final EftBatchPaymentDetailRepository detailRepository;
    private final BankAccountWhitelistRepository bankAccountWhitelistRepository;
    private final BankHeadOfficeRepository headOfficeRepository;
    private final AccEpaymentRepository epaymentRepository;
    private final SimpleDateFormat yyMMdd = new SimpleDateFormat("yyMMdd");
    private final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    public List<EftBatchPaymentDetail> save(EftBatchPayment batch, List<EftBatchPaymentDetail> details) {
        Optional<BankAccountWhitelist> whitelist = bankAccountWhitelistRepository.findByAccountIdAndBankId(batch.getDebtorAccount(), batch.getDebtorAgent());
        if (whitelist.isEmpty()) {
            throw new CustomException("Bank Account Whitelist not found. {" + batch.getDebtorAgent() + "} {" + batch.getDebtorAccount() + "}");
        }
        Date now = new Date();
        int date = Integer.parseInt(yyMMdd.format(now));
        long time = Long.parseLong(yyyyMMddHHmmss.format(now));
        int sn = repository.findMaxSn(date, batch.getPoCode());
        BigInteger id = sn < 10 ? new BigInteger(date + batch.getPoCode() + "00" + sn) : sn < 100 ? new BigInteger(date + batch.getPoCode() + "0" + sn) : new BigInteger(date + "" + batch.getPoCode() + sn);
        batch.setId(id);
        batch.setSn(sn);
        batch.setReceiveDate(date);
        batch.setReceiveTime(time);
        int rowNo = 1;

        List<EftBatchPaymentDetail> objDetail = new ArrayList<>();
        for (EftBatchPaymentDetail detail : details) {
            try {
                Optional<BankHeadOffice> headOffice = headOfficeRepository.findById(detail.getCreditorAgent());
                if (headOffice.isEmpty()) {
                    throw new CustomException("Creditor Agent " + detail.getCreditorAgent() + " " + detail.getCreditorName() + " not found ");
                }
                Optional<EftBatchPaymentDetail> optional = detailRepository.findByInstructionId(detail.getInstructionId());
                if (optional.isPresent()) {
                    epaymentRepository.updateStatusProcessing(Long.parseLong(detail.getInstructionId()));
                    continue;
                }
                BigInteger detailsId = new BigInteger((rowNo < 10) ? id + "000" + rowNo : (rowNo < 100) ? id + "00" + rowNo : (rowNo < 1000) ? id + "0" + rowNo : id + "" + rowNo);
                detail.setId(detailsId);
                detail.setEftBatchPaymentId(id);
                objDetail.add(detail);
            } catch (Exception e) {
                log.info("{} {}", e.getMessage(), detail.getInstructionId());
            }
            rowNo++;
        }
        batch.setOffus(objDetail.size());
        repository.save(batch);
        detailRepository.saveAll(objDetail);

        if (rowNo == 1) {
            throw new CustomException("Payment Detail not found");
        }
        return objDetail;
    }
}
