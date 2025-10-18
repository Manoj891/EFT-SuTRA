package com.fcgo.eft.sutra.dto.res;

import java.math.BigDecimal;

public interface EftPaymentRequestDetailProjection {
    String getId();

    long getAddenda1();

    String getAddenda2();

    String getAddenda3();

    String getAddenda4();

    BigDecimal getAmount();

    String getCreditorAccount();

    String getCreditorAgent();

    String getCreditorName();

    String getEndToEndId();

    String getInstructionId();

    String getNchlCreditStatus();

    String getNchlResponseMessage();

    String getNchlTransactionType();

    java.time.LocalDateTime getRecDate();

    String getRefId();

    String getRemarks();

    String getDebtorAgent();

    String getDebtorAccount();

    String getDebtorName();

    String getCategoryPurpose();

    String getDebtorBranch();

    Long getPoCode();
    Integer getTryCount();
}


