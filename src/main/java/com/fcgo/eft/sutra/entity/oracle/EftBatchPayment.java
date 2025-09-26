package com.fcgo.eft.sutra.entity.oracle;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "EFT_PAYMENT_BATCH",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_EFT_PAYMENT_BATCH_BATCH_ID", columnNames = {"BATCH_ID"}),
                @UniqueConstraint(name = "UK_EFT_PAYMENT_BATCH_PO_CODE_DATE_SN", columnNames = {"PO_CODE", "RECEIVE_DATE", "SN"})
        },
        indexes = {
                @Index(name = "IDX_EFT_PAYMENT_BATCH_RECEIVE_TIME", columnList = "RECEIVE_TIME"),
                @Index(name = "IDX_EFT_PAYMENT_BATCH_OFFUS", columnList = "OFFUS"),
                @Index(name = "IDX_EFT_PAYMENT_BATCH_OFFUS_PUSHED", columnList = "OFFUS_PUSHED")
        }
)
public class EftBatchPayment {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER(30)")
    private BigInteger id;
    @Column(name = "PO_CODE", columnDefinition = "NUMBER(15) NOT NULL", updatable = false)
    private Long poCode;
    @Column(name = "RECEIVE_DATE", columnDefinition = "NUMBER(6) NOT NULL", updatable = false)
    private Integer receiveDate;
    @Column(name = "SN", columnDefinition = "NUMBER(6) NOT NULL", updatable = false)
    private Integer sn;

    @Column(name = "RECEIVE_TIME", columnDefinition = "NUMBER(15)", nullable = false, updatable = false)
    private Long receiveTime;

    @Column(name = "BATCH_ID", length = 25, updatable = false)
    private String batchId;

    @Column(name = "DEBTOR_AGENT", length = 6, updatable = false)
    private String debtorAgent;
    @Column(name = "DEBTOR_ACCOUNT", length = 20, updatable = false)
    private String debtorAccount;

    @Column(name = "DEBTOR_NAME", length = 160, updatable = false)
    private String debtorName;
    @Column(name = "CATEGORY_PURPOSE", length = 10, updatable = false)
    private String categoryPurpose;
    @Column(name = "OFFUS", columnDefinition = "NUMBER(4)", updatable = false)
    private Integer offus;
    @Column(name = "OFFUS_PUSHED", length = 1, updatable = false)
    private String offusPushed;
    @Column(name = "CREATED_BY", length = 30, updatable = false)
    private String createdBy;
    @Column(name = "DEPLOYMENT_TYPE", length = 4)
    private String deploymentType;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
            value = {
                    @JoinColumn(
                            name = "DEBTOR_AGENT",
                            referencedColumnName = "BANK_ID",
                            insertable = false,
                            updatable = false
                    ),
                    @JoinColumn(
                            name = "DEBTOR_ACCOUNT",
                            referencedColumnName = "ACCOUNT_ID",
                            insertable = false,
                            updatable = false
                    )
            },
            foreignKey = @ForeignKey(name = "FK_EFT_DEBTOR_BANK_AND_ACCOUNT_NOT_WHITELIST")
    )
    private BankAccountWhitelist bankAccountWhitelist;

    @Override
    public String toString() {
        return "{"
                + "\"offus\":\"" + offus + "\","
                + "\"receiveDate\":\"" + receiveDate + "\","
                + "\"categoryPurpose\":\"" + categoryPurpose + "\","
                + "\"debtorName\":\"" + debtorName + "\","
                + "\"debtorAccount\":\"" + debtorAccount + "\","
                + "\"debtorAgent\":\"" + debtorAgent + "\","
                + "\"poCode\":\"" + poCode + "\","
                + "\"batchId\":\"" + batchId + "\","
                + "\"id\":\"" + id + "\""
                + "}";

    }
}

