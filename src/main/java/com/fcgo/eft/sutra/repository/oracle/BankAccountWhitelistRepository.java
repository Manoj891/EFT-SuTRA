package com.fcgo.eft.sutra.repository.oracle;


import com.fcgo.eft.sutra.dto.req.TransactionId;
import com.fcgo.eft.sutra.entity.oracle.BankAccountWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface BankAccountWhitelistRepository extends JpaRepository<BankAccountWhitelist, String> {
    @Query(value = "SELECT NCHL_TRANSACTION_TYPE AS transactionType,EFT_BATCH_PAYMENT_ID AS paymentId FROM EFT_PAYMENT_BATCH_DETAIL WHERE INSTRUCTION_ID=?1", nativeQuery = true)
    Optional<TransactionId> findNchlTransactionType(String id);

    @Query(value = "SELECT BATCH_ID from EFT_PAYMENT_BATCH where ID=?1", nativeQuery = true)
    String findBatchId(BigInteger id);

    Optional<BankAccountWhitelist> findByAccountIdAndBankId(String accountId, String bankId);
}
