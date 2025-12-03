package com.fcgo.eft.sutra.repository;

import com.fcgo.eft.sutra.dto.res.NchlReconciledRes;
import com.fcgo.eft.sutra.entity.NchlReconciled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NchlReconciledRepository extends JpaRepository<NchlReconciled, Long> {

    @Query(value = "SELECT D.INSTRUCTION_ID AS INSTRUCTION_ID FROM EFT_PAYMENT_BATCH_DETAIL D LEFT JOIN NCHL_RECONCILED N on D.INSTRUCTION_ID = N.INSTRUCTION_ID WHERE D.NCHL_CREDIT_STATUS IS NOT NULL AND NCHL_TRANSACTION_TYPE = 'ONUS' AND (PUSHED is null OR PUSHED = 'N') ORDER BY INSTRUCTION_ID", nativeQuery = true)
    List<String> findRealTimePendingInstructionId();

    @Query(value = "SELECT BATCH_ID FROM EFT_PAYMENT_BATCH_DETAIL D JOIN EFT_PAYMENT_BATCH B ON D.EFT_BATCH_PAYMENT_ID = B.ID LEFT JOIN NCHL_RECONCILED N ON D.INSTRUCTION_ID = N.INSTRUCTION_ID WHERE D.NCHL_CREDIT_STATUS IS NOT NULL AND NCHL_TRANSACTION_TYPE = 'OFFUS' AND NCHL_CREDIT_STATUS='SENT' AND (PUSHED IS NULL OR PUSHED = 'N') GROUP BY BATCH_ID ORDER BY BATCH_ID", nativeQuery = true)
    List<String> findNonRealTimePendingBatchId();

    @Query(value = "SELECT INSTRUCTION_ID instructionId,DEBIT_STATUS debitStatus,DEBIT_MESSAGE debitMessage,CREDIT_STATUS creditStatus,CREDIT_MESSAGE creditMessage FROM NCHL_RECONCILED WHERE PUSHED='N' AND (PUSHED_DATETIME IS NULL OR PUSHED_DATETIME<=?1) ORDER BY INSTRUCTION_ID FETCH FIRST 100 ROWS ONLY", nativeQuery = true)
    List<NchlReconciledRes> findByPushed(long datetime);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL E SET E.NCHL_CREDIT_STATUS = 'SENT',NCHL_PUSHED_DATE_TIME=?1 WHERE (E.NCHL_CREDIT_STATUS IS NULL OR E.NCHL_CREDIT_STATUS = 'BUILD') AND EXISTS ( SELECT 1 FROM NCHL_RECONCILED R WHERE R.INSTRUCTION_ID = E.INSTRUCTION_ID )", nativeQuery = true)
    void updateMissingStatusSent(long nowTime);
}
