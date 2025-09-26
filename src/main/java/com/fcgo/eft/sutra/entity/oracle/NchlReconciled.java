package com.fcgo.eft.sutra.entity.oracle;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "NCHL_RECONCILED", indexes = {
        @Index(name = "INDEX_NCHL_RECONCILED_PUSHED", columnList = "PUSHED"),
        @Index(name = "INDEX_NCHL_RECONCILED_CREDIT_STATUS", columnList = "CREDIT_STATUS")
})
public class NchlReconciled {
    @Id
    @Column(name = "INSTRUCTION_ID", length = 30, nullable = false)
    private long instructionId;

    @Column(name = "DEBIT_STATUS", length = 10)
    private String debitStatus;
    @Column(name = "DEBIT_MESSAGE")
    private String debitMessage;


    @Column(name = "CREDIT_STATUS", length = 10)
    private String creditStatus;
    @Column(name = "CREDIT_MESSAGE")
    private String creditMessage;
    @Column(name = "RECONCILED_DATE", length = 30)
    private Date recDate;

    @Column(name = "PUSHED", length = 1, updatable = false)
    private String pushed;
    @Column(name = "TRANSACTION_ID", length = 30, nullable = false)
    private String transactionId;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
