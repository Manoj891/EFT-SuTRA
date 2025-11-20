package com.fcgo.eft.sutra.repository.mssql;

import com.fcgo.eft.sutra.entity.mssql.AccEpayment;
import com.fcgo.eft.sutra.test.AccEpaymentRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AccEpaymentRepository extends JpaRepository<AccEpayment, Long> {


    @Query(value = "select transtatus,pstatus,StatusMessage message from acc_epayment where eftno=?1", nativeQuery = true)
    Optional<AccEpaymentRes> findByEftNumber(long eftNumber);



    @Query(value = "select jm.eftno from acc_epayment as jm inner join acc_epayment_resendlog as b on jm.id = b.id inner join acc_journalmaster as jv on jv.jid = jm.journalid where jm.fyid = 22", nativeQuery = true)
    List<Long> eftNumberRejected();

    Optional<AccEpayment> findByEftNo(long eftNo);

    @Query(value = "select eftno from acc_epayment where transtatus =2 and pstatus=2  and paymentdate < dateadd(minute, -30, getdate())", nativeQuery = true)
    List<Long> updateSuccessEPayment();

    @Modifying
    @Transactional
    @Query(value = "update acc_epayment set transtatus=2,pstatus=2,paymentdate=GETDATE() where eftno=?1", nativeQuery = true)
    void updateStatusProcessing(long instructionId);

    @Modifying
    @Transactional
    @Query(value = "update acc_epayment set transtatus=1,pstatus=0 where eftno=?1", nativeQuery = true)
    void updateRevertInSuTra(long instructionId);

    @Modifying
    @Transactional
    @Query(value = "update acc_epayment set transtatus=2,pstatus=1,StatusMessage=?1, paymentdate=?2 where eftno=?3", nativeQuery = true)
    void updateSuccessEPayment(String message, Date settelmentDate, long instructionId);

    @Modifying
    @Transactional
    @Query(value = "update acc_epayment set pstatus=-1, StatusMessage=?1 where eftno=?2", nativeQuery = true)
    void updateFailureEPayment(String message, long instructionId);

    @Modifying
    @Transactional
    @Query(value = "update acc_epayment set StatusMessage=?1 where eftno=?2", nativeQuery = true)
    void updateMessage(String message, long instructionId);
}
