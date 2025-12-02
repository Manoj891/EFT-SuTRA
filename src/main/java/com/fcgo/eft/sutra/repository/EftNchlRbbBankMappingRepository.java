package com.fcgo.eft.sutra.repository;

import com.fcgo.eft.sutra.dto.req.BankMap;
import com.fcgo.eft.sutra.entity.EftNchlRbbBankMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EftNchlRbbBankMappingRepository extends JpaRepository<EftNchlRbbBankMapping, String> {
    @Query(value = "select BANK_ID nchlCode,NRB_BANK_CODE nrbCode,BANK_NAME bankName from EFT_NCHL_RBB_BANK_MAPPING M join BANK_HEAD_OFFICE H on M.BANK_CODE=H.BANK_ID", nativeQuery = true)
    List<BankMap> findBankMap();
}
