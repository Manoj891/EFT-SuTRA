package com.fcgo.eft.sutra.dto.req;

import java.math.BigInteger;

public interface TransactionId {
    String getTransactionType();

    BigInteger getPaymentId();
}
