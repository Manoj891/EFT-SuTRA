package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<NchlReconciled, Long> {
    @Query(value = "SELECT CREDIT_STATUS, DEBTOR_NAME, DEBTOR_ACCOUNT, CREDITOR_NAME, CREDITOR_ACCOUNT, END_TO_END_ID, ADDENDA4,AMOUNT,D.INSTRUCTION_ID,BATCH_ID,RECONCILED_DATE FROM NCHL_RECONCILED R JOIN EFT_PAYMENT_BATCH_DETAIL D ON R.INSTRUCTION_ID = D.INSTRUCTION_ID JOIN EFT_SUTRA.EFT_PAYMENT_BATCH EPB on D.EFT_BATCH_PAYMENT_ID = EPB.ID  WHERE R.INSTRUCTION_ID = ?1", nativeQuery = true)
    Map<String, Object> findNchlTransactionSuccess(long instructionId);
}
