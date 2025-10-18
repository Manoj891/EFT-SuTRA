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


    @Query(value = "SELECT D.INSTRUCTION_ID FROM EFT_PAYMENT_BATCH_DETAIL D LEFT JOIN NCHL_RECONCILED N on D.INSTRUCTION_ID = N.INSTRUCTION_ID WHERE NCHL_TRANSACTION_TYPE = 'ONUS' AND (PUSHED is null OR PUSHED = 'N')", nativeQuery = true)
    List<String> findRealTimePendingInstructionId();


    @Query(value = "SELECT BATCH_ID FROM EFT_PAYMENT_BATCH_DETAIL D JOIN EFT_PAYMENT_BATCH B ON D.EFT_BATCH_PAYMENT_ID = B.ID LEFT JOIN NCHL_RECONCILED N ON D.INSTRUCTION_ID = N.INSTRUCTION_ID WHERE NCHL_TRANSACTION_TYPE = 'OFFUS' AND NCHL_CREDIT_STATUS='SENT' AND (PUSHED IS NULL OR PUSHED = 'N') GROUP BY BATCH_ID", nativeQuery = true)
    List<String> findNonRealTimePendingBatchId();

    @Query(value = "SELECT D.INSTRUCTION_ID FROM EFT_PAYMENT_BATCH M JOIN EFT_PAYMENT_BATCH_DETAIL D ON M.ID = D.EFT_BATCH_PAYMENT_ID LEFT JOIN NCHL_RECONCILED R ON D.INSTRUCTION_ID = R.INSTRUCTION_ID WHERE R.INSTRUCTION_ID IS NULL AND M.RECEIVE_DATE <= 251014 AND (NCHL_PUSHED_DATE_TIME <= 20251015000000 OR (NCHL_CREDIT_STATUS IS NULL AND NCHL_PUSHED_DATE_TIME IS NULL))", nativeQuery = true)
    List<String> findPendingInstructionId();


    @Modifying
    @Transactional
    @Query(value = "insert into NCHL_RECONCILED(INSTRUCTION_ID, CREDIT_MESSAGE, CREDIT_STATUS, DEBIT_MESSAGE, DEBIT_STATUS, PUSHED, RECONCILED_DATE, TRANSACTION_ID, UPDATED_AT) select INSTRUCTION_ID, 'NOT FOUND' CREDIT_MESSAGE, '1000' CREDIT_STATUS, 'Verify with bank' DEBIT_MESSAGE, '097' DEBIT_STATUS, 'N' PUSHED, SYSDATE RECONCILED_DATE, '-' TRANSACTION_ID, SYSDATE UPDATED_AT from EFT_PAYMENT_BATCH_DETAIL where INSTRUCTION_ID=?1", nativeQuery = true)
    void insertNchlReconciled(String id);

    @Modifying
    @Transactional
    @Query(value = "update EFT_PAYMENT_BATCH_DETAIL set NCHL_TRANSACTION_TYPE='MNL' WHERE INSTRUCTION_ID=?1", nativeQuery = true)
    void updateNchlReconciled(String id);


    Optional<NchlReconciled> findByInstructionId(long instructionId);

    @Query(value = "SELECT M.BATCH_ID FROM NCHL_RECONCILED R join EFT_PAYMENT_BATCH_DETAIL B on B.INSTRUCTION_ID = R.INSTRUCTION_ID join EFT_PAYMENT_BATCH M on B.EFT_BATCH_PAYMENT_ID = M.ID where DEBIT_STATUS != '000' AND NCHL_TRANSACTION_TYPE = 'OFFUS' AND PUSHED='N' group by M.BATCH_ID ", nativeQuery = true)
    List<String> findByNonRealTimePendingTransactionId();

    @Query(value = "SELECT SUBSTR(TO_CHAR(B.NCHL_PUSHED_DATE_TIME), 1, 8) AS PUSH_DATE FROM EFT_PAYMENT_BATCH_DETAIL B LEFT JOIN NCHL_RECONCILED R ON B.INSTRUCTION_ID = R.INSTRUCTION_ID WHERE B.NCHL_CREDIT_STATUS IN ('SENT', 'BUILD') AND (R.CREDIT_STATUS IS NULL OR R.CREDIT_STATUS NOT IN ('ACSE', '000', 'RJCT')) AND (R.PUSHED = 'N' OR R.PUSHED IS NULL) AND B.NCHL_PUSHED_DATE_TIME IS NOT NULL GROUP BY SUBSTR(TO_CHAR(B.NCHL_PUSHED_DATE_TIME), 1, 8) ORDER BY PUSH_DATE", nativeQuery = true)
    List<String> findByPendingDate();

    @Query(value = "SELECT * FROM NCHL_RECONCILED WHERE PUSHED='N' AND CREDIT_STATUS NOT IN('ACTC','SENT','ACSP')", nativeQuery = true)
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
