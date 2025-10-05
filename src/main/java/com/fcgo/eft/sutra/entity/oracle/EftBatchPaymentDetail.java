package com.fcgo.eft.sutra.entity.oracle;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "EFT_PAYMENT_BATCH_DETAIL",
        indexes = {
                @Index(name = "INDEX_EFT_PAYMENT_BATCH_DETAIL_INSTRUCTION_ID", columnList = "INSTRUCTION_ID", unique = true),
                @Index(name = "INDEX_EFT_PAYMENT_BATCH_DETAIL_EFT_BATCH_PAYMENT_ID", columnList = "EFT_BATCH_PAYMENT_ID"),
                @Index(name = "INDEX_EFT_PAYMENT_BATCH_DETAIL_STATUS", columnList = "NCHL_CREDIT_STATUS"),
                @Index(name = "INDEX_EFT_PAYMENT_BATCH_DETAIL_NCHL_PUSHED_DATE_TIME", columnList = "NCHL_PUSHED_DATE_TIME"),
                @Index(name = "INDEX_EFT_PAYMENT_BATCH_DETAIL_TRANSACTION_TYPE", columnList = "NCHL_TRANSACTION_TYPE")})
public class EftBatchPaymentDetail {
    @Id
    @Column(name = "ID", columnDefinition = "NUMBER(35)")
    private BigInteger id;
    @Column(name = "EFT_BATCH_PAYMENT_ID", columnDefinition = "NUMBER(30)", nullable = false)
    private BigInteger eftBatchPaymentId;
    @Column(name = "INSTRUCTION_ID", length = 30, nullable = false)
    private String instructionId;
    @Column(name = "CREDITOR_ACCOUNT", length = 20, nullable = false)
    private String creditorAccount;
    @Column(name = "CREDITOR_AGENT", length = 6, nullable = false)
    private String creditorAgent;
    @Column(name = "CREDITOR_NAME", length = 150, nullable = false)
    private String creditorName;
    @Column(name = "END_TO_END_ID", length = 100, nullable = false)
    private String endToEndId;


    @Column(name = "AMOUNT", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "ADDENDA1", nullable = false)
    private Long addenda1;
    @Column(name = "ADDENDA2", length = 10)
    private String addenda2;
    @Column(name = "ADDENDA3", length = 100)
    private String addenda3;
    @Column(name = "ADDENDA4", length = 100)
    private String addenda4;
    @Column(name = "REF_ID", length = 100)
    private String refId;
    @Column(name = "REMARKS", length = 100)
    private String remarks;
    @Column(name = "NCHL_TRANSACTION_TYPE", length = 10, nullable = false, updatable = false)
    private String nchlTransactionType;
    @Column(name = "NCHL_CREDIT_STATUS", length = 10, updatable = false)
    private String nchlCreditStatus;
    @Column(name = "NCHL_PUSHED_DATE_TIME", columnDefinition = "NUMBER(14)", updatable = false)
    private Long nchlPushedDateTime;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREDITOR_AGENT", referencedColumnName = "BANK_ID", foreignKey = @ForeignKey(name = "FK_EFT_PAYMENT_BATCH_DETAIL_CREDITOR_BANK_NOT_FOUND"), insertable = false, updatable = false)
    private BankHeadOffice creditorBank;


    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @JoinColumn(name = "EFT_BATCH_PAYMENT_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EftBatchPayment eftBatchPayment;

    @Override
    public String toString() {
        return "{"
                + "\"id\":\"" + id + "\","
                + "\"eftBatchPaymentId\":\"" + eftBatchPaymentId + "\","
                + "\"instructionId\":\"" + instructionId + "\","
                + "\"creditorAccount\":\"" + creditorAccount + "\","
                + "\"creditorAgent\":\"" + creditorAgent + "\","
                + "\"creditorName\":\"" + creditorName + "\","
                + "\"endToEndId\":\"" + endToEndId + "\","
                + "\"nchlTransactionType\":\"" + nchlTransactionType + "\","
                + "\"nchlCreditStatus\":\"" + nchlCreditStatus + "\","
                + "\"amount\":\"" + amount + "\","
                + "\"addenda1\":\"" + addenda1 + "\","
                + "\"addenda2\":\"" + addenda2 + "\","
                + "\"addenda3\":\"" + addenda3 + "\","
                + "\"addenda4\":\"" + addenda4 + "\","
                + "\"refId\":\"" + refId + "\","
                + "\"remarks\":\"" + remarks + "\","
                + "}";
    }

}
