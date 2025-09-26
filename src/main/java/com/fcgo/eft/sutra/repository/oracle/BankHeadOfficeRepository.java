package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.dto.req.BankMap;
import com.fcgo.eft.sutra.entity.oracle.BankHeadOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BankHeadOfficeRepository extends JpaRepository<BankHeadOffice, String> {
    @Query(value = "select BANK_ID nchlCode,NRB_BANK_CODE nrbCode,BANK_NAME bankName from EFT_NCHL_RBB_BANK_MAPPING M join BANK_HEAD_OFFICE H on M.BANK_CODE=H.BANK_ID", nativeQuery = true)
    List<BankMap> findBankMap();


    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL SET NCHL_CREDIT_STATUS = NULL WHERE NCHL_CREDIT_STATUS = 'BUILD' AND NCHL_PUSHED_DATE <= SYSDATE - (5 / 24)", nativeQuery = true)
    void updatePaymentPendingStatusDetail();

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH M SET M.OFFUS_PUSHED = 'N' WHERE M.OFFUS_PUSHED = 'Y' AND M.OFFUS > 0 AND EXISTS ( SELECT 1 FROM EFT_PAYMENT_BATCH_DETAIL D WHERE D.EFT_BATCH_PAYMENT_ID = M.ID AND D.NCHL_CREDIT_STATUS IS NULL)", nativeQuery = true)
    void updatePaymentPendingStatusMaster();

}
