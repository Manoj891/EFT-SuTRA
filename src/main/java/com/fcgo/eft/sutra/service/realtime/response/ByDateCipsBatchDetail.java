package com.fcgo.eft.sutra.service.realtime.response;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "RECONCILED_TRANSACTION")
public class ByDateCipsBatchDetail {
    @Id
    @Column(name = "ENTITY_ID", length = 50)
    private String entityId;
    @Column(name = "ID", columnDefinition = "NUMBER(20)")
    private Long id;
    @Column(name = "BATCH_ID", length = 40)
    private String batchId;
    @Column(name = "REC_DATE",length = 10)
    private String recDate;
    @Column(name = "BATCH_CRNCY",length = 10)
    private String batchCrncy;
    @Column(name = "CATEGORY_PURPOSE",length = 10)
    private String categoryPurpose;
    @Column(name = "DEBTOR_AGENT",length = 10)
    private String debtorAgent;
    @Column(name = "DEBTOR_BRANCH",length = 10)
    private String debtorBranch;
    @Column(name = "DEBTOR_NAME")
    private String debtorName;
    @Column(name = "DEBTOR_ACCOUNT",length = 20)
    private String debtorAccount;
    @Column(name = "DEBIT_STATUS",length = 10)
    private String debitStatus;
    @Column(name = "REASON_DESC",length = 150)
    private String debitReasonDesc;
    @Column(name = "SETTLEMENT_DATE",length = 20)
    private String settlementDate;
    @Column(name = "TXN_RESPONSE",length = 150)
    private String txnResponse;
}
