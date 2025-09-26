package com.fcgo.eft.sutra.dto.res;

public interface PaymentStatus {
    long getId();

    String getInstructionId();

    String getPaymentStatus();

    String getPaymentDate();

    String getStatusMessage();

    String getBatchId();

    String getNewBatchId();

    String getNchlId();

    String getNchlResponseMessage();

    String getNchlTransactionType();

    String getPoCode();
}
