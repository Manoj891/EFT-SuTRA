package com.fcgo.eft.sutra.repository.mssql;

import com.fcgo.eft.sutra.entity.mssql.AccEpayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional
public interface AccEpaymentRepository extends JpaRepository<AccEpayment, Long> {
    @Modifying
    @Query(value = "update acc_epayment set pstatus=1,StatusMessage=?1, paymentdate=?2 where eftno=?3", nativeQuery = true)
    void updateSuccessEPayment(String message, Date settelmentDate, long instructionId);

    @Modifying
    @Query(value = "update epayment_trans_log set crStatusCode=?1 where instructionId=?2", nativeQuery = true)
    void updateEPaymentLog(String message, long instructionId);

    @Modifying
    @Query(value = "update acc_epayment set pstatus=-1, StatusMessage=?1 where eftno=?2", nativeQuery = true)
    void updateFailureEPayment(String message, long instructionId);

    @Modifying
    @Query(value = "update acc_epayment set StatusMessage=?1 where eftno=?2", nativeQuery = true)
    void updateMessage(String message, long instructionId);
}
