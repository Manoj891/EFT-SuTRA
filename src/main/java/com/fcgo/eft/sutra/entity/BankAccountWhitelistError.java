package com.fcgo.eft.sutra.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "BANK_ACCOUNT_WHITELIST_ERROR")
public class BankAccountWhitelistError {


    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "ERROR")
    private String error;
    @Column(name = "UPDATED_AT")
    private Long updatedAt;


    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = new Date().getTime();
    }
}