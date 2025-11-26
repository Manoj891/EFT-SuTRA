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
            status = (status == null ? "" : status);
            String debitStatus = reconciled.getDebitStatus();
            debitStatus = (debitStatus == null ? "000" : debitStatus);
            if (!debitStatus.equals("000")) {
                updateFailureStatus("CR."+reconciled.getCreditMessage() + ", DR." + reconciled.getDebitMessage(), instructionId);
            } else if (status.equals("000") || status.equals("ACSC")) {
                updateSuccessStatus(reconciled.getRecDate(), instructionId);
            } else if (status.equals("RJCT")
                    || status.equals("1001")
                    || status.equals("1000")
                    || status.equals("909")
                    || status.equals("099")
                    || status.equals("096")
                    || status.equals("-01")
                    || status.equals("-02")
                    || status.equals("-04")
                    || status.equals("-0")
                    || status.equals("030")
                    || status.equals("119")) {
                updateFailureStatus("CR. " + reconciled.getCreditMessage() + " DR. " + reconciled.getDebitMessage(), instructionId);
            } else {
                String message = (reconciled.getCreditMessage() == null ? status : reconciled.getCreditMessage()) + " DR. " + reconciled.getDebitMessage();
                log.info("Message updated {} {} ", message, instructionId);
                epaymentRepository.updateMessage(message, instructionId);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    private void updateFailureStatus(String creditMessage, long instructionId) {
        if (creditMessage == null || creditMessage.length() < 3) creditMessage = "Clearly message not found.";
        else if (creditMessage.length() > 500) creditMessage = creditMessage.substring(0, 495);
        epaymentRepository.updateFailureEPayment(creditMessage, instructionId);
        repository.updateStatus(String.valueOf(instructionId));
        log.info("Updated reconciled status: {} failure", instructionId);
    }

    private void updateSuccessStatus( Date recDate, long instructionId) {
        epaymentRepository.updateSuccessEPayment( "SUCCESS", recDate, instructionId);
        repository.updateStatus(String.valueOf(instructionId));
        log.info("Updated reconciled status: {} Success", instructionId);
    }
}
