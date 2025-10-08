package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.RemoteIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RemoteIpRepository extends JpaRepository<RemoteIp, String> {
    Optional<RemoteIp> findByIdAndUsername(String id, String username);

    @Query(value = "SELECT R.INSTRUCTION_ID FROM NCHL_RECONCILED R LEFT JOIN EFT_PAYMENT_BATCH_DETAIL D ON D.INSTRUCTION_ID = R.INSTRUCTION_ID WHERE D.INSTRUCTION_ID IS NULL AND RECONCILED_DATE >= TO_DATE('2025-09-22', 'YYYY-MM-DD')", nativeQuery = true)
    List<String> findByPendingTransactionId();

}
