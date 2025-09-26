package com.fcgo.eft.sutra.dto.res;


import java.math.BigDecimal;
import java.util.Date;

public interface TransactionDetailByInstructionId {
    String getNchlCreditStatus();

    String getNchlResponseMessage();

    BigDecimal getAmount();

    String getCreditorName();

    String getCreditorAccount();

    String getInstructionId();

    Date getTransferEdate();

    String getDebtorName();

    String getDebtorAccount();

    String getDebitStatus();

    String getEndToEndId();

}
