package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.PoCodeMapped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoCodeMappedRepository extends JpaRepository<PoCodeMapped, Long> {
    @Query(value = "SELECT NVL(MAX(CODE),'1000')+1 FROM PO_CODE_MAPPED", nativeQuery = true)
    int findMaxCode();

    @Query(value = "SELECT C.PO_CODE FROM EFT_PAYMENT_BATCH C LEFT JOIN PO_CODE_MAPPED M ON C.PO_CODE = M.PO_CODE where M.PO_CODE IS NULL GROUP BY C.PO_CODE ORDER BY C.PO_CODE", nativeQuery = true)
    List<Long> findAllByPoCode();
}

