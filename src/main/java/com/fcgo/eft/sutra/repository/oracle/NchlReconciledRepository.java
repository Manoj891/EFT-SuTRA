package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface NchlReconciledRepository extends JpaRepository<NchlReconciled, String> {
    @Query(value = "SELECT D.INSTRUCTION_ID AS INSTRUCTION_ID FROM EFT_PAYMENT_BATCH_DETAIL D LEFT JOIN NCHL_RECONCILED N on D.INSTRUCTION_ID = N.INSTRUCTION_ID WHERE D.NCHL_CREDIT_STATUS IS NOT NULL AND NCHL_TRANSACTION_TYPE = 'ONUS' AND (PUSHED is null OR PUSHED = 'N') ORDER BY INSTRUCTION_ID", nativeQuery = true)
    List<String> findRealTimePendingInstructionId();

    @Query(value = "SELECT BATCH_ID FROM EFT_PAYMENT_BATCH_DETAIL D JOIN EFT_PAYMENT_BATCH B ON D.EFT_BATCH_PAYMENT_ID = B.ID LEFT JOIN NCHL_RECONCILED N ON D.INSTRUCTION_ID = N.INSTRUCTION_ID WHERE D.NCHL_CREDIT_STATUS IS NOT NULL AND NCHL_TRANSACTION_TYPE = 'OFFUS' AND NCHL_CREDIT_STATUS='SENT' AND (PUSHED IS NULL OR PUSHED = 'N') GROUP BY BATCH_ID ORDER BY BATCH_ID", nativeQuery = true)
    List<String> findNonRealTimePendingBatchId();

    Optional<NchlReconciled> findByInstructionId(long instructionId);

    @Query(value = "SELECT * FROM NCHL_RECONCILED WHERE PUSHED='N'", nativeQuery = true)
    List<NchlReconciled> findByPushed(String pushed);

    @Modifying
    @Transactional
    @Query(value = "UPDATE NCHL_RECONCILED  SET PUSHED = 'Y' WHERE INSTRUCTION_ID = ?1", nativeQuery = true)
    void updateStatus(String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL set NCHL_CREDIT_STATUS='SENT' WHERE ID IN( SELECT D.ID FROM EFT_PAYMENT_BATCH_DETAIL D join NCHL_RECONCILED R on D.INSTRUCTION_ID = R.INSTRUCTION_ID where (NCHL_CREDIT_STATUS IS NULL OR NCHL_CREDIT_STATUS='BUILD'))", nativeQuery = true)
    void updateMissingStatusSent();
}
