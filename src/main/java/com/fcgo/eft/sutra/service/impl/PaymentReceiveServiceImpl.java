package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.dto.req.BankMap;
import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.req.EftPaymentRequestDetailReq;
import com.fcgo.eft.sutra.dto.req.PaymentRequest;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.entity.oracle.EftBatchPayment;
import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.exception.PermissionDeniedException;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.security.AuthenticatedUser;
import com.fcgo.eft.sutra.security.AuthenticationFacade;
import com.fcgo.eft.sutra.service.PaymentReceiveService;
import com.fcgo.eft.sutra.util.CategoryPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentReceiveServiceImpl implements PaymentReceiveService {
    private final AuthenticationFacade facade;
    private final PaymentSaveService repository;
    private final AccEpaymentRepository epaymentRepository;

    private final Map<String, String> bankMap = new HashMap<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void setBankMaps(List<BankMap> bankMaps) {
        bankMaps.forEach(b -> bankMap.put(b.getNrbCode().trim(), b.getNchlCode().trim()));
    }

    @Override
    public Map<String, String> getBankMap() {
        return bankMap;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public PaymentReceiveStatus paymentReceive(EftPaymentReceive receive) {
        AuthenticatedUser user = facade.getAuthentication();
        if (!(user.getPaymentUser().equals("Y") && user.getAppName().equals("SuTRA")))
            throw new PermissionDeniedException();
        Date now = new Date();
        PaymentRequest b = receive.getPaymentRequest();
        String debtorAgent = bankMap.get(b.getDebtorAgent());
        String debtorName = b.getDebtorName().trim();
        String debtorAccount = b.getDebtorAccount().trim();
        if (debtorAgent == null) {
            throw new CustomException("Debtor Bank Code " + b.getDebtorAgent() + " Name: " + debtorAccount + " Not Found");
        }
        String batchId = b.getBatchId();
        EftBatchPayment batch = EftBatchPayment.builder().batchId(batchId).poCode(b.getPoCode()).debtorAccount(debtorAccount).debtorName(debtorName).debtorAgent(debtorAgent).categoryPurpose(CategoryPurpose.get(b.getCategoryPurpose())).offus(0).offusPushed("N").build();

        List<EftBatchPaymentDetail> details = new ArrayList<>();
        int offus = 0, onus = 0;
        for (EftPaymentRequestDetailReq dto : receive.getEftPaymentRequestDetail()) {
            String nchlTransactionType;
            String creditorAgent = bankMap.get(dto.getCreditorAgent().trim());
            String creditorAccount = dto.getCreditorAccount().trim();
            String creditorName = dto.getCreditorName().trim();
            if (creditorAgent == null) {
                throw new CustomException("Creditor Bank Code " + dto.getCreditorAgent().trim() + " Name " + creditorName + " Not Found");
            }
            if (debtorAgent.equals(creditorAgent)) {
                nchlTransactionType = "ONUS";
                onus++;
            } else {
                nchlTransactionType = "OFFUS";
                offus++;
            }
            String addenda4 = (dto.getAddenda4() == null || dto.getAddenda4().isEmpty()) ? dto.getInstructionId() : dto.getAddenda4();
            String addenda3 = (dto.getAddenda3() == null || dto.getAddenda3().isEmpty()) ? dto.getInstructionId() : dto.getAddenda3();
            details.add(EftBatchPaymentDetail.builder().instructionId(dto.getInstructionId()).creditorAccount(creditorAccount.trim()).creditorAgent(creditorAgent.trim()).creditorName(creditorName.trim()).endToEndId(dto.getEndToEndId()).nchlTransactionType(nchlTransactionType).amount(dto.getAmount()).addenda1(now.getTime()).addenda2(dateFormat.format(now)).addenda3(addenda3).addenda4(addenda4).refId(dto.getRefId() == null ? dto.getInstructionId() : dto.getRefId()).remarks(dto.getRemarks() == null ? dto.getInstructionId() : dto.getRemarks()).nchlCreditStatus(null).build());
        }

        batch.setDeploymentType(user.getDeploymentType());
        batch.setCreatedBy(user.getAppName());
        List<EftBatchPaymentDetail> list = repository.save(batch, details, offus);
        list.forEach(detail -> epaymentRepository.updateStatusProcessing(Long.parseLong(detail.getInstructionId())));
        log.info("Commited. BATCH ID:{} {} ITEM RECEIVED", batch.getBatchId(), list.size());
        return PaymentReceiveStatus.builder().offus(offus).onus(onus).build();
    }
}
