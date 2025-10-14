package com.fcgo.eft.sutra.entity.oracle;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "BANK_ACCOUNT_WHITELIST")
public class BankAccountWhitelist {

    @EmbeddedId
    private BankAccountWhitelistPk pk;

    @Column(name = "BRANCH_ID", length = 6)
    private String branchId;

    @Column(name = "ACCOUNT_NAME", length = 150)
    private String accountName;

    @Column(name = "BANK_NAME", length = 100)
    private String bankName;

    @Column(name = "STATUS", length = 15)
    private String status;

    @Column(name = "RCRE_TIME", length = 30)
    private String rcreTime;

    @Column(name = "UPDATED_AT")
    private Long updatedAt;

    @Column(name = "BANK_ID", length = 6, insertable = false, updatable = false)
    private String bankId;
    @Column(name = "ACCOUNT_ID", length = 20, insertable = false, updatable = false)
    private String accountId;


}