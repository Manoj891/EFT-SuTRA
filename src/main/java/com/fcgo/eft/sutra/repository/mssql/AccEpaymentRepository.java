package com.fcgo.eft.sutra.repository.mssql;

import com.fcgo.eft.sutra.dto.EftStatus;
import com.fcgo.eft.sutra.entity.mssql.AccEpayment;
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

    Optional<AccEpayment> findByEftNo(long eftNo);

    @Query(value = "select eftno from acc_epayment where transtatus =2 and pstatus=2  and paymentdate < dateadd(minute, -30, getdate())", nativeQuery = true)
    List<Long> updateSuccessEPayment();

    @Modifying
    @Transactional
    @Query(value = "update acc_epayment set transtatus=2,pstatus=2,paymentdate=GETDATE() where eftno=?1", nativeQuery = true)
    void updateStatusProcessing(long instructionId);
}
