package com.fcgo.eft.sutra.repository;

import com.fcgo.eft.sutra.entity.BankAccountWhitelistError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountWhitelistErrorRepository extends JpaRepository<BankAccountWhitelistError, Integer> {
}
