package com.fcgo.eft.sutra.repository;

import com.fcgo.eft.sutra.entity.ReconciledTransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciledTransactionDetailRepository extends JpaRepository<ReconciledTransactionDetail, String> {
}
