package com.fcgo.eft.sutra.entity.oracle;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "RECONCILED_TRANSACTION_DETAIL")
public class ReconciledTransactionDetail {
    @Id
    @Column(name = "ENTITY_ID", length = 100)
    private String entityId;
    @Column(name = "ID",length = 50)
    private String id;
    @Column(name = "REC_DATE")
    private Date recDate;
    @Column(name = "INSTRUCTION_ID")
    private Long instructionId;
    @Column(name = "END_TO_END_ID", length = 100)
    private String endToEndId;
    @Column(name = "CHARGE_LIABILITY", length = 10)
    private String chargeLiability;
    @Column(name = "PURPOSE", length = 10)
    private String purpose;
    @Column(name = "CREDIT_STATUS", length = 10)
    private String creditStatus;
    @Column(name = "REASON_CODE", length = 100)
    private String reasonCode;
    @Column(name = "REMARKS", length = 100)
    private String remarks;
    @Column(name = "PARTICULARS", length = 100)
    private String particulars;
    @Column(name = "REASON_DESC", length = 100)
    private String reasonDesc;
    @Column(name = "AMOUNT", columnDefinition = "NUMBER(15,3)")
    private double amount;
    @Column(name = "CHARGE_AMOUNT", columnDefinition = "NUMBER(15,3)")
    private double chargeAmount;
    @Column(name = "CREDITOR_AGENT", length = 10)
    private String creditorAgent;
    @Column(name = "CREDITOR_BRANCH", length = 10)
    private String creditorBranch;
    @Column(name = "CREDITOR_NAME", length = 150)
    private String creditorName;
    @Column(name = "CREDITOR_ACCOUNT", length = 20)
    private String creditorAccount;
    @Column(name = "ADDENDA1", columnDefinition = "NUMBER(20)")
    private long addenda1;
    @Column(name = "ADDENDA2", length = 100)
    private String addenda2;
    @Column(name = "ADDENDA3", length = 100)
    private String addenda3;
    @Column(name = "ADDENDA4", length = 100)
    private String addenda4;
    @Column(name = "REF_ID", length = 100)
    private String refId;

    @Column(name = "RECONCILED_TRANSACTION_Id",length = 50, nullable = false)
    private String reconciledTransactionId;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @JoinColumn(name = "RECONCILED_TRANSACTION_Id", referencedColumnName = "ENTITY_ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ReconciledTransaction realTimeTransaction;

}
