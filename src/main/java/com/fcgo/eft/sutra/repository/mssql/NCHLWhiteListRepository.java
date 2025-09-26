package com.fcgo.eft.sutra.repository.mssql;

import com.fcgo.eft.sutra.entity.mssql.NCHLWhiteList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NCHLWhiteListRepository extends JpaRepository<NCHLWhiteList, Long> {

    @Query(value = "select max(id)+1 from NCHLWhiteList", nativeQuery = true)
    long findMaxId();

    Optional<NCHLWhiteList> findByBankIdAndAccountNumber(String bankId, String accountNumber);


}
