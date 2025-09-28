package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TransactionStatusUpdate {
    private final NchlReconciledRepository repository;
    private final AccEpaymentRepository epaymentRepository;

    public void update(NchlReconciled reconciled) {
        try {
            long instructionId = reconciled.getInstructionId();
            String status = reconciled.getCreditStatus();
            String debitStatus = reconciled.getDebitStatus();
            epaymentRepository.updateEPaymentLog(status, instructionId);
            if (!debitStatus.equals("000")) {
                updateFailureStatus(reconciled.getCreditMessage() + " " + reconciled.getDebitMessage(), instructionId);
            } else if (status.equals("000") || status.equals("ACSC")) {
                updateSuccessStatus(reconciled.getCreditMessage(), reconciled.getRecDate(), instructionId);
            } else if (status.equals("RJCT")
                    || status.equals("1000")
                    || status.equals("096")
                    || status.equals("1001")
                    || status.equals("099")
                    || status.equals("909")
                    || status.equals("-01")
                    || status.equals("-04")
                    || status.equals("119")) {
                updateFailureStatus(reconciled.getCreditMessage() + " " + reconciled.getDebitMessage(), instructionId);
            } else {
                log.info("Message updated {} {} ", reconciled.getCreditMessage(), instructionId);
                String message = reconciled.getCreditMessage() + " " + reconciled.getDebitMessage();
                if (message.length() > 500) message = message.substring(0, 495);
                if (message.equalsIgnoreCase("SUCCESS")) {
                    updateSuccessStatus(reconciled.getCreditMessage(), reconciled.getRecDate(), instructionId);
                } else {
                    epaymentRepository.updateMessage(message, instructionId);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    private void updateFailureStatus(String creditMessage, long instructionId) {
        if (creditMessage.length() > 500) creditMessage = creditMessage.substring(0, 495);
        epaymentRepository.updateFailureEPayment(creditMessage, instructionId);
        repository.updateStatus(String.valueOf(instructionId));
        log.info("Updated reconciled status: {} failure", instructionId);
    }

    private void updateSuccessStatus(String creditMessage, Date recDate, long instructionId) {
        epaymentRepository.updateSuccessEPayment(creditMessage, recDate, instructionId);
        repository.updateStatus(String.valueOf(instructionId));
        log.info("Updated reconciled status: {} Success", instructionId);
    }
}
