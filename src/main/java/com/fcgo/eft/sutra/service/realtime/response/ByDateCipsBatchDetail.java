package com.fcgo.eft.sutra.service.realtime.response;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Entity
//@Table(name = "RECONCILED_TRANSACTION_MASTER")
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
    private String batchCrncy;
    private String categoryPurpose;
    private String debtorAgent;
    private String debtorBranch;
    private String debtorName;
    private String debtorAccount;
    private String debitStatus;
    private String debitReasonDesc;
    private String settlementDate;
    private String txnResponse;
}
