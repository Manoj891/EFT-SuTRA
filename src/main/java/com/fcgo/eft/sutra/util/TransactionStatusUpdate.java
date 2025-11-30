package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
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
    private final DbPrimary dbPrimary;
    private final StringToJsonNode jsonNode;


    public void update(NchlReconciled reconciled, long datetime) {
        try {
            long instructionId = reconciled.getInstructionId();
            String status = reconciled.getCreditStatus();
            status = (status == null ? "" : status);
            String debitStatus = reconciled.getDebitStatus();
            debitStatus = (debitStatus == null ? "000" : debitStatus);
            if (!debitStatus.equals("000")) {
                updateFailureStatus("CR." + reconciled.getCreditMessage() + ", DR." + reconciled.getDebitMessage(), instructionId, datetime);
            } else if (status.equals("000") || status.equals("ACSC")) {
                updateSuccessStatus(reconciled.getRecDate(), instructionId, datetime);
            } else if (getRejectStatus(status)) {
                updateFailureStatus("CR. (" + status + ") " + reconciled.getCreditMessage() + " DR. " + reconciled.getDebitMessage(), instructionId, datetime);
            } else {
                String message = (reconciled.getCreditMessage() == null ?
                        status : reconciled.getCreditMessage()) + " DR. " + reconciled.getDebitMessage()
                        .replace("'", "");
                log.info("Message updated {} {} ", message, instructionId);
                if (dbPrimary.update("update acc_epayment set StatusMessage='" + message + "' where eftno=" + instructionId) > 0) {
                    repository.updateDateTime(datetime, instructionId);
                    log.info("Updated reconciled status: {} failure", instructionId);
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean getRejectStatus(String code) {
        return (code.equals("RJCT")
                || code.equals("1001")
                || code.equals("1000")
                || code.equals("909")
                || code.equals("907")
                || code.equals("906")
                || code.equals("119")
                || code.equals("099")
                || code.equals("096")
                || code.equals("030")
                || code.equals("-0")
                || code.equals("-01")
                || code.equals("-02")
                || code.equals("-04")
                || code.equals("E001")
                || code.equals("E002")
                || code.equals("E003")
                || code.equals("E004")
                || code.equals("E005")
                || code.equals("E006")
                || code.equals("E007")
                || code.equals("E008")
                || code.equals("E009")
                || code.equals("E010")
                || code.equals("E011")
                || code.equals("E012"));
    }

    private void updateFailureStatus(String creditMessage, long instructionId, long datetime) {
        if (creditMessage == null || creditMessage.length() < 3) creditMessage = "Clearly message not found.";
        else if (creditMessage.length() > 500) creditMessage = creditMessage.substring(0, 495);
        try {
            int i = dbPrimary.update("update acc_epayment set pstatus=-1, StatusMessage='" + creditMessage + "' where eftno=" + instructionId);
            if (i > 0) {
                repository.updateStatus(datetime, instructionId);
                log.info("Updated reconciled status: {} failure", instructionId);
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private void updateSuccessStatus(Date recDate, long instructionId, long datetime) {
        try {
            int i = dbPrimary.update("update acc_epayment set transtatus=2,pstatus=1,StatusMessage='SUCCESS', paymentdate='" + jsonNode.getDateTime().format(recDate) + "' where eftno=" + instructionId);
            if (i > 0) {
                repository.updateStatus(datetime, instructionId);
                log.info("Updated reconciled status: {} Success", instructionId);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
