package com.fcgo.eft.sutra.entity.oracle;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;


import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Embeddable
public class BankAccountWhitelistPk implements Serializable {
    @Column(name = "BANK_ID", length = 6)
    private String bankId;
    @Column(name = "ACCOUNT_ID", length = 20)
    private String accountId;
}
