package com.fcgo.eft.sutra.repository;

import com.fcgo.eft.sutra.entity.ReconciledTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciledTransactionRepository extends JpaRepository<ReconciledTransaction, String> {
}
