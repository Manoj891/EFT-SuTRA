package com.fcgo.eft.sutra.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "BANK_HEAD_OFFICE")
public class BankHeadOffice {

    @Id
    @Column(name = "BANK_ID", length = 6, nullable = false)
    private String bankId;

    @Column(name = "HEAD_OFFICE_ID", length = 6, nullable = false)
    private String headOfficeId;
}
