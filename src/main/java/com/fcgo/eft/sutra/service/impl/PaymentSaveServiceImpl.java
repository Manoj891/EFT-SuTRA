package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.dto.req.*;
import com.fcgo.eft.sutra.dto.res.PaymentSaved;
import com.fcgo.eft.sutra.entity.oracle.BankAccountWhitelist;
import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.BankAccountWhitelistRepository;
import com.fcgo.eft.sutra.repository.oracle.EftBatchPaymentDetailRepository;
import com.fcgo.eft.sutra.repository.oracle.EftPaymentRequestRepository;
import com.fcgo.eft.sutra.security.AuthenticatedUser;
import com.fcgo.eft.sutra.service.PaymentSaveService;
import com.fcgo.eft.sutra.util.CategoryPurpose;
import com.fcgo.eft.sutra.util.IsProdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSaveServiceImpl implements PaymentSaveService {
    private final EftPaymentRequestRepository repository;
    private final EftBatchPaymentDetailRepository detailRepository;
    private final BankAccountWhitelistRepository bankAccountWhitelistRepository;
    private final AccEpaymentRepository epaymentRepository;
    private final IsProdService isProdService;
    private final StringToJsonNode jsonNode;

    private final Map<Long, Boolean> status = new HashMap<>();
    private final Map<String, String> bankMap = new HashMap<>();

    @Override
    public void busy(long poCode, boolean busy) {
        status.put(poCode, busy);
    }

    @Override
    public Map<Long, Boolean> getStatus() {
        return status;
    }

    @Override
    public Map<String, String> getBankMap() {
        return bankMap;
    }

    @Override
    public void setBankMaps(List<BankMap> bankMaps) {
        bankMaps.forEach(b -> bankMap.put(b.getNrbCode().trim(), b.getNchlCode().trim()));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
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
        int date = Integer.parseInt(jsonNode.getYyMMdd().format(now));
        long time = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(now));
        int sn = repository.findMaxSn(date, b.getPoCode());
        String tempId;
        if (sn < 10) tempId = date + "" + b.getPoCode() + "000" + sn;
        else if (sn < 100) tempId = date + "" + b.getPoCode() + "00" + sn;
        else tempId = date + "" + b.getPoCode() + "0" + sn;
        BigInteger id = new BigInteger(tempId);
        String category = CategoryPurpose.get(b.getCategoryPurpose());
        boolean categoryUpdate = false;
        repository.insert(id, batchId, category, user.getAppName(), debtorAccount, debtorAgent, debtorName, user.getDeploymentType(), 0, "N", b.getPoCode(), date, time, sn);
        List<EftBatchPaymentDetail> details = new ArrayList<>();
        int rowNo = 1;
        long addenda1 = now.getTime();
        String addenda2 = jsonNode.getDateFormat().format(now);
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
                    if (!(category.equalsIgnoreCase("GTAX") || category.equalsIgnoreCase("GSAL"))) {
                        category = "GSAL";
                        categoryUpdate = true;
                    }
                    onus++;
                } else {
                    nchlTransactionType = "OFFUS";
                    offus++;
                }
                String addenda4 = (dto.getAddenda4() == null || dto.getAddenda4().isEmpty()) ? dto.getInstructionId() : dto.getAddenda4();
                String addenda3 = isProdService.getProdIpAddress();
                String tempDetailsId;
                if (rowNo < 10) tempDetailsId = tempId + "000" + rowNo;
                else if (rowNo < 100) tempDetailsId = tempId + "00" + rowNo;
                else if (rowNo < 1000) tempDetailsId = tempId + "0" + rowNo;
                else tempDetailsId = tempId + rowNo;
                details.add(EftBatchPaymentDetail.builder().id(new BigInteger(tempDetailsId)).eftBatchPaymentId(id).instructionId(dto.getInstructionId()).creditorAccount(creditorAccount.trim()).creditorAgent(creditorAgent.trim()).creditorName(creditorName.trim()).endToEndId(dto.getEndToEndId()).amount(dto.getAmount()).addenda1(addenda1).addenda2(addenda2).addenda3(addenda3).addenda4(addenda4).refId(dto.getRefId() == null ? dto.getInstructionId() : dto.getRefId()).remarks(dto.getRemarks() == null ? dto.getInstructionId() : dto.getRemarks()).nchlTransactionType(nchlTransactionType).build());
                rowNo++;
            }
        }
        PaymentSaved saved = PaymentSaved.builder().details(detailRepository.saveAll(details)).onus(onus).offus(offus).build();
        if (offus > 0) {
            repository.update(offus, id);
        }
        if (categoryUpdate) {
            repository.updateCategory(category, id);
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public PaymentSaved save(PaymentRequestNew receive, AuthenticatedUser user) {
        String debtorAgent = bankMap.get(receive.getDebtorAgent());
        String debtorName = receive.getDebtorName().trim();
        String debtorAccount = receive.getDebtorAccount().trim();
        if (debtorAgent == null) {
            status.put(receive.getPoCode(), false);
            throw new CustomException("Debtor Bank Code " + receive.getDebtorAgent() + " Name: " + debtorAccount + " Not Found");
        }
        Optional<BankAccountWhitelist> whitelist = bankAccountWhitelistRepository.findByAccountIdAndBankId(debtorAccount, debtorAgent);
        if (whitelist.isEmpty()) {
            status.put(receive.getPoCode(), false);
            throw new CustomException("Bank Account not Whitelisted.");
        }
        String batchId = receive.getBatchId();
        Date now = new Date();
        int date = Integer.parseInt(jsonNode.getYyMMdd().format(now));
        long time = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(now));
        int sn = repository.findMaxSn(date, receive.getPoCode());
        String tempId;
        if (sn < 10) tempId = date + "" + receive.getPoCode() + "000" + sn;
        else if (sn < 100) tempId = date + "" + receive.getPoCode() + "00" + sn;
        else tempId = date + "" + receive.getPoCode() + "0" + sn;
        BigInteger id = new BigInteger(tempId);
        String category = CategoryPurpose.get(receive.getCategoryPurpose());
        boolean categoryUpdate = false;
        repository.insert(id, batchId, category, user.getAppName(), debtorAccount, debtorAgent, debtorName, user.getDeploymentType(), 0, "N", receive.getPoCode(), date, time, sn);
        List<EftBatchPaymentDetail> details = new ArrayList<>();
        int rowNo = 1;
        long addenda1 = now.getTime();
        String addenda2 = jsonNode.getDateFormat().format(now);
        int offus = 0, onus = 0;
        for (EftPaymentRequestDetailReq dto : receive.getDetails()) {
            Optional<EftBatchPaymentDetail> optional = detailRepository.findByInstructionId(dto.getInstructionId());
            if (optional.isPresent()) {
                epaymentRepository.updateStatusProcessing(Long.parseLong(dto.getInstructionId()));
            } else {
                String creditorAgent = bankMap.get(dto.getCreditorAgent().trim());
                String creditorAccount = dto.getCreditorAccount().trim();
                String creditorName = dto.getCreditorName().trim();
                if (creditorAgent == null) {
                    status.put(receive.getPoCode(), false);
                    throw new CustomException("Creditor Bank Code " + dto.getCreditorAgent().trim() + " Name " + creditorName + " Not Found");
                }
                String nchlTransactionType;
                if (debtorAgent.equals(creditorAgent)) {
                    nchlTransactionType = "ONUS";
                    onus++;
                    if (!(category.equalsIgnoreCase("GTAX") || category.equalsIgnoreCase("GSAL"))) {
                        category = "GSAL";
                        categoryUpdate = true;
                    }
                } else {
                    nchlTransactionType = "OFFUS";
                    offus++;
                }
                String addenda4 = (dto.getAddenda4() == null || dto.getAddenda4().isEmpty()) ? dto.getInstructionId() : dto.getAddenda4();
                String addenda3 = isProdService.getProdIpAddress();
                String tempDetailsId;
                if (rowNo < 10) tempDetailsId = tempId + "000" + rowNo;
                else if (rowNo < 100) tempDetailsId = tempId + "00" + rowNo;
                else if (rowNo < 1000) tempDetailsId = tempId + "0" + rowNo;
                else tempDetailsId = tempId + rowNo;
                details.add(EftBatchPaymentDetail.builder().id(new BigInteger(tempDetailsId)).eftBatchPaymentId(id).instructionId(dto.getInstructionId()).creditorAccount(creditorAccount.trim()).creditorAgent(creditorAgent.trim()).creditorName(creditorName.trim()).endToEndId(dto.getEndToEndId()).amount(dto.getAmount()).addenda1(addenda1).addenda2(addenda2).addenda3(addenda3).addenda4(addenda4).refId(dto.getRefId() == null ? dto.getInstructionId() : dto.getRefId()).remarks(dto.getRemarks() == null ? dto.getInstructionId() : dto.getRemarks()).nchlTransactionType(nchlTransactionType).build());
                rowNo++;
            }
        }
        PaymentSaved saved = PaymentSaved.builder().details(detailRepository.saveAll(details)).onus(onus).offus(offus).build();
        if (offus > 0) {
            repository.update(offus, id);
        }
        if (categoryUpdate) {
            repository.updateCategory(category, id);
        }
        return saved;

    }

}
