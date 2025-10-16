package com.fcgo.eft.sutra.repository.oracle;

import com.fcgo.eft.sutra.dto.res.EftPaymentRequestDetailProjection;
import com.fcgo.eft.sutra.dto.res.PaymentBatchPendingRes;
import com.fcgo.eft.sutra.entity.oracle.EftBatchPaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface EftBatchPaymentDetailRepository extends JpaRepository<EftBatchPaymentDetail, String> {
    Optional<EftBatchPaymentDetail> findByInstructionId(String instructionId);

    @Query(value = "SELECT M.DEBTOR_AGENT AS agent, M.DEBTOR_ACCOUNT AS account, W.BRANCH_ID AS branch, M.DEBTOR_NAME AS name, M.CATEGORY_PURPOSE AS purpose, M.BATCH_ID AS batchId, M.ID AS id,OFFUS AS offus,M.PO_CODE poCode FROM EFT_PAYMENT_BATCH M JOIN BANK_ACCOUNT_WHITELIST W on M.DEBTOR_ACCOUNT = W.ACCOUNT_ID and M.DEBTOR_AGENT = W.BANK_ID where OFFUS > 0 AND OFFUS_PUSHED = 'N' AND M.RECEIVE_DATE>='251015' ORDER BY RECEIVE_TIME FETCH FIRST 50 ROWS ONLY", nativeQuery = true)
    List<PaymentBatchPendingRes> findPaymentNonRealPendingRes();

    List<EftBatchPaymentDetail> findByEftBatchPaymentIdAndNchlTransactionTypeAndNchlCreditStatusNullAndNchlPushedDateTimeNull(BigInteger eftBatchPaymentId, String nchlTransactionType);

    @Query(value = "SELECT D.ID AS id, D.ADDENDA1 AS addenda1, D.ADDENDA2 AS addenda2, D.ADDENDA3 AS addenda3, D.ADDENDA4 AS addenda4, D.AMOUNT AS amount, D.CREDITOR_ACCOUNT AS creditorAccount, D.CREDITOR_AGENT AS creditorAgent, D.CREDITOR_NAME AS creditorName, D.END_TO_END_ID AS endToEndId, D.INSTRUCTION_ID AS instructionId, D.NCHL_CREDIT_STATUS AS nchlCreditStatus,  D.NCHL_TRANSACTION_TYPE AS nchlTransactionType, D.REF_ID AS refId, D.REMARKS AS remarks, M.DEBTOR_AGENT AS debtorAgent, M.DEBTOR_ACCOUNT AS debtorAccount, M.DEBTOR_NAME AS debtorName, M.CATEGORY_PURPOSE AS categoryPurpose, W.BRANCH_ID AS debtorBranch,M.PO_CODE poCode FROM EFT_PAYMENT_BATCH_DETAIL D JOIN EFT_PAYMENT_BATCH M ON D.EFT_BATCH_PAYMENT_ID = M.ID JOIN BANK_ACCOUNT_WHITELIST W ON M.DEBTOR_ACCOUNT = W.ACCOUNT_ID and M.DEBTOR_AGENT = W.BANK_ID WHERE D.NCHL_CREDIT_STATUS IS NULL AND D.NCHL_PUSHED_DATE_TIME IS NULL AND D.NCHL_TRANSACTION_TYPE = 'ONUS' AND M.RECEIVE_DATE>='251015' ORDER BY M.RECEIVE_TIME FETCH FIRST 20 ROWS ONLY", nativeQuery = true)
    List<EftPaymentRequestDetailProjection> findRealTimePending();

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL SET NCHL_CREDIT_STATUS = ?1,NCHL_PUSHED_DATE_TIME=?2 WHERE EFT_BATCH_PAYMENT_ID = ?3 AND NCHL_TRANSACTION_TYPE='OFFUS'", nativeQuery = true)
    void updateBatchBuild(String status, long dateTime, BigInteger masterId);
    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL SET NCHL_CREDIT_STATUS = ?1 WHERE EFT_BATCH_PAYMENT_ID = ?2 AND NCHL_TRANSACTION_TYPE='OFFUS'", nativeQuery = true)
    void updateBatchBuild(String status, BigInteger masterId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH SET OFFUS_PUSHED = 'Y' WHERE ID = ?1 ", nativeQuery = true)
    void updateBatchBuild(BigInteger masterId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL SET NCHL_CREDIT_STATUS = ?1 WHERE  INSTRUCTION_ID = ?2", nativeQuery = true)
    void updateNchlStatusByInstructionId(String status, String instructionId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE EFT_PAYMENT_BATCH_DETAIL SET NCHL_CREDIT_STATUS = ?1,NCHL_PUSHED_DATE_TIME=?2 WHERE  INSTRUCTION_ID = ?3", nativeQuery = true)
    void updateNchlStatusByInstructionId(String status,long dateTime, String instructionId);


   }
