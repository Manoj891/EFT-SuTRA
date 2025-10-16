package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.EftBatchPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Repository
public interface EftPaymentRequestRepository extends JpaRepository<EftBatchPayment, String> {
    @Query(value = "SELECT NVL(MAX(SN),0)+1 FROM EFT_PAYMENT_BATCH WHERE RECEIVE_DATE=?1 AND PO_CODE=?2", nativeQuery = true)
    Integer findMaxSn(Integer date, Long poCode);


    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH SET OFFUS=?1 WHERE ID=?2", nativeQuery = true)
    void insert(BigInteger id, String batchId, String categoryPurpose, String createdBy, String debtorAccount, String debtorAgent, String debtorName, String deploymentType, int offus, String offusPushed, long poCode, int receiveDate, long receiveTime, int sn );
    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH SET OFFUS=?1 WHERE ID=?2", nativeQuery = true)
    void update(int offus, BigInteger id);
}
