package com.fcgo.eft.sutra.entity.mssql;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "NCHLWhiteList")
public class NCHLWhiteList {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "userid")
    private String userId;

    @Column(name = "bankid")
    private String bankId;

    @Column(name = "branchid")
    private String branchId;

    @Column(name = "accountnumber")
    private String accountNumber;

    @Column(name = "accountname")
    private String accountName;

    @Column(name = "status")
    private String status;

    @Column(name = "accountholdername")
    private String accountHolderName;

    @Column(name = "accountholderid")
    private String accountHolderId;

    @Column(name = "adminid")
    private Integer adminId;

    @Column(name = "orgid")
    private Integer orgId;
}
