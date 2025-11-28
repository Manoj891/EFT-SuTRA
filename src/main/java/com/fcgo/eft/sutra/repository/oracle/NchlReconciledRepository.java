package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface NchlReconciledRepository extends JpaRepository<NchlReconciled, Long> {


    @Query(value = "SELECT TRY_COUNT, TRY_TIME, R.CREDIT_MESSAGE, D.INSTRUCTION_ID FROM EFT_PAYMENT_BATCH_DETAIL D JOIN NCHL_RECONCILED R on D.INSTRUCTION_ID = R.INSTRUCTION_ID where PUSHED = 'N' AND R.CREDIT_STATUS = 'SENT' AND NCHL_TRANSACTION_TYPE = 'ONUS' AND TRY_COUNT >= 15", nativeQuery = true)
    List<Map<String, Object>> findTryTimeOutToReject();

    @Query(value = "SELECT D.ID FROM EFT_PAYMENT_BATCH_DETAIL D JOIN EFT_PAYMENT_BATCH M ON D.EFT_BATCH_PAYMENT_ID = M.ID JOIN NCHL_RECONCILED R ON D.INSTRUCTION_ID = R.INSTRUCTION_ID WHERE R.CREDIT_STATUS = 'SENT' AND PUSHED = 'N' AND D.NCHL_CREDIT_STATUS = 'SENT' AND D.NCHL_TRANSACTION_TYPE = 'ONUS' AND TRY_COUNT < 15 AND M.RECEIVE_DATE >= 251017", nativeQuery = true)
    List<BigInteger> findTryForNextAttempt();

    @Query(value = "SELECT D.INSTRUCTION_ID AS INSTRUCTION_ID FROM EFT_PAYMENT_BATCH_DETAIL D LEFT JOIN NCHL_RECONCILED N on D.INSTRUCTION_ID = N.INSTRUCTION_ID WHERE D.NCHL_CREDIT_STATUS IS NOT NULL AND NCHL_TRANSACTION_TYPE = 'ONUS' AND (PUSHED is null OR PUSHED = 'N') ORDER BY INSTRUCTION_ID", nativeQuery = true)
    List<String> findRealTimePendingInstructionId();

    @Query(value = "SELECT BATCH_ID FROM EFT_PAYMENT_BATCH_DETAIL D JOIN EFT_PAYMENT_BATCH B ON D.EFT_BATCH_PAYMENT_ID = B.ID LEFT JOIN NCHL_RECONCILED N ON D.INSTRUCTION_ID = N.INSTRUCTION_ID WHERE D.NCHL_CREDIT_STATUS IS NOT NULL AND NCHL_TRANSACTION_TYPE = 'OFFUS' AND NCHL_CREDIT_STATUS='SENT' AND (PUSHED IS NULL OR PUSHED = 'N') GROUP BY BATCH_ID ORDER BY BATCH_ID", nativeQuery = true)
    List<String> findNonRealTimePendingBatchId();

    Optional<NchlReconciled> findByInstructionId(long instructionId);

    @Query(value = "SELECT * FROM NCHL_RECONCILED WHERE PUSHED='N'", nativeQuery = true)
    List<NchlReconciled> findByPushed(String pushed);

    @Modifying
    @Transactional
    @Query(value = "UPDATE NCHL_RECONCILED  SET CREDIT_STATUS = ?1,CREDIT_MESSAGE=?2,DEBIT_STATUS=?3,DEBIT_MESSAGE=?4 WHERE INSTRUCTION_ID = ?5 AND CREDIT_STATUS='SENT'", nativeQuery = true)
    void updateRejectTransaction(String creditStatus, String creditMessage, String debitStatus, String debitMessage, long instructionId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE NCHL_RECONCILED  SET PUSHED = 'Y' WHERE INSTRUCTION_ID = ?1", nativeQuery = true)
    void updateStatus(String id);
    @Modifying
    @Transactional
    @Query(value = "UPDATE NCHL_RECONCILED  SET CREDIT_STATUS='1000',DEBIT_STATUS='1000',DEBIT_MESSAGE='Rejected' WHERE INSTRUCTION_ID = ?1", nativeQuery = true)
    void updateManualReject(String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL  SET NCHL_CREDIT_STATUS='SENT',NCHL_PUSHED_DATE_TIME=?1 WHERE INSTRUCTION_ID = ?1", nativeQuery = true)
    void updateManualReject(long time,String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL set NCHL_CREDIT_STATUS='SENT' WHERE ID IN( SELECT D.ID FROM EFT_PAYMENT_BATCH_DETAIL D join NCHL_RECONCILED R on D.INSTRUCTION_ID = R.INSTRUCTION_ID where (NCHL_CREDIT_STATUS IS NULL OR NCHL_CREDIT_STATUS='BUILD'))", nativeQuery = true)
    void updateMissingStatusSent();

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL SET NCHL_CREDIT_STATUS=null,NCHL_PUSHED_DATE_TIME=NULL WHERE ID =?1", nativeQuery = true)
    void missingStatusSent(BigInteger  id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL SET NCHL_CREDIT_STATUS=NULL, NCHL_PUSHED_DATE_TIME=NULL WHERE (NCHL_CREDIT_STATUS IS NULL AND NCHL_PUSHED_DATE_TIME IS NOT NULL) OR (NCHL_CREDIT_STATUS IS NOT NULL AND NCHL_PUSHED_DATE_TIME IS NULL)", nativeQuery = true)
    void missingStatusSent();

}
