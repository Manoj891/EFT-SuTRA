package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface NchlReconciledRepository extends JpaRepository<NchlReconciled, String> {



    @Query(value = "SELECT M.BATCH_ID FROM NCHL_RECONCILED R join EFT_PAYMENT_BATCH_DETAIL B on B.INSTRUCTION_ID = R.INSTRUCTION_ID join EFT_PAYMENT_BATCH M on B.EFT_BATCH_PAYMENT_ID = M.ID where DEBIT_STATUS != '000' AND NCHL_TRANSACTION_TYPE = 'OFFUS' AND PUSHED='N' group by M.BATCH_ID ", nativeQuery = true)
    List<String> findByNonRealTimePendingTransactionId();

    @Query(value = "SELECT INSTRUCTION_ID FROM EFT_PAYMENT_BATCH_DETAIL D where D.NCHL_TRANSACTION_TYPE = 'ONUS' AND NCHL_PUSHED_DATE <= SYSDATE - (1 / 24) and not exists (select 1 from NCHL_RECONCILED R where R.INSTRUCTION_ID = D.INSTRUCTION_ID) FETCH FIRST 100 ROWS ONLY", nativeQuery = true)
    List<String> findByPendingTransactionId();

    @Query(value = "SELECT TO_CHAR(B.NCHL_PUSHED_DATE, 'YYYY-MM-DD') FROM EFT_PAYMENT_BATCH_DETAIL B LEFT JOIN NCHL_RECONCILED R ON B.INSTRUCTION_ID = R.INSTRUCTION_ID WHERE B.NCHL_CREDIT_STATUS IN ('SENT','BUILD') AND (R.CREDIT_STATUS IS NULL OR R.CREDIT_STATUS NOT IN ('ACSE', '000', 'RJCT')) AND (R.PUSHED='N' OR R.PUSHED is null ) group by TO_CHAR(B.NCHL_PUSHED_DATE, 'YYYY-MM-DD')", nativeQuery = true)
    List<String> findByPendingDate();

    @Query(value = "SELECT * FROM NCHL_RECONCILED WHERE PUSHED='N' AND CREDIT_STATUS NOT IN('ACTC','SENT','ACSP')", nativeQuery = true)
    List<NchlReconciled> findByPushed(String pushed);

    @Modifying
    @Transactional
    @Query(value = "UPDATE NCHL_RECONCILED  SET PUSHED = 'Y' WHERE INSTRUCTION_ID = ?1", nativeQuery = true)
    void updateStatus(String id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL  SET NCHL_CREDIT_STATUS = null WHERE INSTRUCTION_ID = ?1", nativeQuery = true)
    void updateForResend(String id);
}
