package com.fcgo.eft.sutra.dto.res;

import java.math.BigInteger;

public interface PaymentBatchPendingRes {
    String getAgent();

    String getAccount();

    String getBranch();

    String getName();

    String getPurpose();

    String getBatchId();

    BigInteger getId();

    int getOffus();
}
