package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.BankMap;
import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.req.EftPaymentRequestDetailReq;
import com.fcgo.eft.sutra.dto.req.PaymentRequest;
import com.fcgo.eft.sutra.dto.res.PaymentSaved;
import com.fcgo.eft.sutra.entity.oracle.BankAccountWhitelist;
import com.fcgo.eft.sutra.entity.oracle.EftBatchPayment;
import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.BankAccountWhitelistRepository;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.repository.oracle.EftPaymentRequestRepository;
import com.fcgo.eft.sutra.security.AuthenticatedUser;
import com.fcgo.eft.sutra.util.CategoryPurpose;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PaymentSaveService {
    private final EftPaymentRequestRepository repository;
    private final EftBatchPaymentDetailRepository detailRepository;
    private final BankAccountWhitelistRepository bankAccountWhitelistRepository;
    private final AccEpaymentRepository epaymentRepository;
    @Getter
    private Map<Long, Boolean> status = new HashMap<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat yyMMdd = new SimpleDateFormat("yyMMdd");
    private final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    @Getter
    private final Map<String, String> bankMap = new HashMap<>();

    public void busy(long poCode, boolean busy) {
        status.put(poCode, busy);
    }


    public void setBankMaps(List<BankMap> bankMaps) {
        bankMaps.forEach(b -> bankMap.put(b.getNrbCode().trim(), b.getNchlCode().trim()));
    }

    public PaymentSaved save(EftPaymentReceive receive, AuthenticatedUser user) {
        PaymentRequest b = receive.getPaymentRequest();

        String debtorAgent = bankMap.get(b.getDebtorAgent());
        String debtorName = b.getDebtorName().trim();
        String debtorAccount = b.getDebtorAccount().trim();

        if (debtorAgent == null) {
            status.put(b.getPoCode(), false);
            throw new CustomException("Debtor Bank Code " + b.getDebtorAgent() + " Name: " + debtorAccount + " Not Found");
        }
        Optional<BankAccountWhitelist> whitelist = bankAccountWhitelistRepository.findByAccountIdAndBankId(debtorAccount, debtorAgent);
        if (whitelist.isEmpty()) {
            status.put(b.getPoCode(), false);
            throw new CustomException("Bank Account not Whitelisted.");
        }
        String batchId = b.getBatchId();
        Date now = new Date();
        int date = Integer.parseInt(yyMMdd.format(now));
        long time = Long.parseLong(yyyyMMddHHmmss.format(now));


        EftBatchPayment batch = EftBatchPayment
                .builder()

                .receiveDate(date)
                .receiveTime(time)
                .batchId(batchId)
                .poCode(b.getPoCode())
                .debtorAccount(debtorAccount)
                .debtorName(debtorName)
                .debtorAgent(debtorAgent)
                .categoryPurpose(CategoryPurpose.get(b.getCategoryPurpose()))
                .offus(0)
                .offusPushed("N")
                .deploymentType(user.getDeploymentType())
                .createdBy(user.getAppName())
                .build();


        int sn = repository.findMaxSn(date, b.getPoCode());
        String tempId;

        if (sn < 10) tempId = date + "" + b.getPoCode() + "000" + sn;
        else if (sn < 100) tempId = date + "" + b.getPoCode() + "00" + sn;
        else tempId = date + "" + b.getPoCode() + "0" + sn;
        batch.setId(new BigInteger(tempId));
        batch.setSn(sn);
        repository.save(batch);
        List<EftBatchPaymentDetail> details = new ArrayList<>();
        int rowNo = 1;
        long addenda1 = now.getTime();
        String addenda2 = dateFormat.format(now);
        int offus = 0, onus = 0;
        for (EftPaymentRequestDetailReq dto : receive.getEftPaymentRequestDetail()) {
            Optional<EftBatchPaymentDetail> optional = detailRepository.findByInstructionId(dto.getInstructionId());
            if (optional.isPresent()) {
                epaymentRepository.updateStatusProcessing(Long.parseLong(dto.getInstructionId()));
            } else {


                String creditorAgent = bankMap.get(dto.getCreditorAgent().trim());
                String creditorAccount = dto.getCreditorAccount().trim();
                String creditorName = dto.getCreditorName().trim();

                if (creditorAgent == null) {
                    status.put(b.getPoCode(), false);
                    throw new CustomException("Creditor Bank Code " + dto.getCreditorAgent().trim() + " Name " + creditorName + " Not Found");
                }
                String nchlTransactionType;
                if (debtorAgent.equals(creditorAgent)) {
                    nchlTransactionType = "ONUS";
                    onus++;
                } else {
                    nchlTransactionType = "OFFUS";
                    offus++;
                }

                String addenda4 = (dto.getAddenda4() == null || dto.getAddenda4().isEmpty()) ? dto.getInstructionId() : dto.getAddenda4();
                String addenda3 = "10.100.193.76";
                String tempDetailsId;
                if (rowNo < 10) tempDetailsId = tempId + "000" + rowNo;
                else if (rowNo < 100) tempDetailsId = tempId + "00" + rowNo;
                else if (rowNo < 1000) tempDetailsId = tempId + "0" + rowNo;
                else tempDetailsId = tempId + rowNo;
                details.add(EftBatchPaymentDetail
                        .builder()
                        .id(new BigInteger(tempDetailsId))
                        .eftBatchPaymentId(batch.getId())
                        .instructionId(dto.getInstructionId())
                        .creditorAccount(creditorAccount.trim())
                        .creditorAgent(creditorAgent.trim())
                        .creditorName(creditorName.trim())
                        .endToEndId(dto.getEndToEndId())
                        .amount(dto.getAmount())
                        .addenda1(addenda1)
                        .addenda2(addenda2)
                        .addenda3(addenda3)
                        .addenda4(addenda4)
                        .refId(dto.getRefId() == null ? dto.getInstructionId() : dto.getRefId())
                        .remarks(dto.getRemarks() == null ? dto.getInstructionId() : dto.getRemarks())
                        .nchlTransactionType(nchlTransactionType)
                        .build());
                rowNo++;
            }

        }
        if (offus > 0) {
            batch.setOffus(offus);
            repository.save(batch);
        }
        return PaymentSaved.builder().details(detailRepository.saveAll(details)).onus(onus).offus(offus).build();
    }

}
