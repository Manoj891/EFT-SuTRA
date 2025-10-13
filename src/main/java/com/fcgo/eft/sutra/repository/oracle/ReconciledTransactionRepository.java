package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.entity.oracle.ReconciledTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciledTransactionRepository extends JpaRepository<ReconciledTransaction, String> {
}
