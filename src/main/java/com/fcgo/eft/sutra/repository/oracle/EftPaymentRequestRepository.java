package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.EftBatchPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EftPaymentRequestRepository extends JpaRepository<EftBatchPayment, String> {
    @Query(value = "SELECT NVL(MAX(SN),0)+1 FROM EFT_PAYMENT_BATCH WHERE RECEIVE_DATE=?1 AND PO_CODE=?2", nativeQuery = true)
    Integer findMaxSn(Integer date, Long poCode);
}
