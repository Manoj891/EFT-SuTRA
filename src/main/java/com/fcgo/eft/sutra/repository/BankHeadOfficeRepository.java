package com.fcgo.eft.sutra.repository;

import com.fcgo.eft.sutra.entity.BankHeadOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BankHeadOfficeRepository extends JpaRepository<BankHeadOffice, String> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL E SET E.NCHL_CREDIT_STATUS = 'SENT', E.NCHL_PUSHED_DATE_TIME = ?1 WHERE E.NCHL_CREDIT_STATUS = 'BUILD' AND E.NCHL_PUSHED_DATE_TIME BETWEEN ?2 AND ?3 AND EXISTS ( SELECT 1 FROM NCHL_RECONCILED R WHERE R.INSTRUCTION_ID = E.INSTRUCTION_ID )", nativeQuery = true)
    void updatePaymentPendingToSent(long nowTime ,long startTime, long dateTime);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL E SET E.NCHL_CREDIT_STATUS= NULL, E.NCHL_PUSHED_DATE_TIME=NULL WHERE E.NCHL_CREDIT_STATUS = 'BUILD' AND E.NCHL_PUSHED_DATE_TIME BETWEEN ?1 AND ?2 AND NOT EXISTS (SELECT 1 FROM NCHL_RECONCILED R WHERE R.INSTRUCTION_ID = E.INSTRUCTION_ID)", nativeQuery = true)
    void updatePaymentPendingStatusDetail(long startTime, long dateTime);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH M SET M.OFFUS_PUSHED = 'N' WHERE M.OFFUS_PUSHED = 'Y' AND M.OFFUS > 0 AND EXISTS (SELECT 1 FROM EFT_PAYMENT_BATCH_DETAIL D WHERE D.EFT_BATCH_PAYMENT_ID = M.ID AND D.NCHL_CREDIT_STATUS IS NULL)", nativeQuery = true)
    void updatePaymentPendingStatusMaster();



}
