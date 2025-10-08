package com.fcgo.eft.sutra.entity.mssql;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "acc_epayment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccEpayment {
    @Id
    @Column(name = "eftno")
    private Long eftNo;

    @Column(name = "fyid", nullable = false)
    private Long fyId;

    @Column(name = "adminid", nullable = false)
    private Long adminId;

    @Column(name = "orgid", nullable = false)
    private Long orgId;

    @Column(name = "journalid", nullable = false)
    private Long journalId;


    @Column(name = "senderbankcode", nullable = false)
    private Long senderBankCode;

    @Column(name = "senderbankname", nullable = false, length = 100)
    private String senderBankName;

    @Column(name = "senderbranchcode", nullable = false)
    private Long senderBranchCode;

    @Column(name = "senderbranch", nullable = false, length = 50)
    private String senderBranch;

    @Column(name = "senderaccountno", nullable = false, length = 50)
    private String senderAccountNo;

    @Column(name = "senderaccountname", nullable = false, length = 150)
    private String senderAccountName;

    @Column(name = "receivertype", nullable = false)
    private Long receiverType;

    @Column(name = "receiverid", nullable = false)
    private Long receiverId;

    @Column(name = "receiverbankcode", nullable = false)
    private Long receiverBankCode;

    @Column(name = "receiverbankid", nullable = false)
    private Long receiverBankId;

    @Column(name = "receiverbank", nullable = false, length = 100)
    private String receiverBank;

    @Column(name = "receiverbranchcode", nullable = false)
    private Long receiverBranchCode;

    @Column(name = "receiverbranch", nullable = false, length = 50)
    private String receiverBranch;

    @Column(name = "receiveraccountno", nullable = false, length = 50)
    private String receiverAccountNo;

    @Column(name = "receivername", nullable = false, length = 150)
    private String receiverName;

    @Column(name = "tamount", precision = 18, scale = 2, nullable = false)
    private BigDecimal tAmount;

    @Column(name = "entrydate", nullable = false)
    private LocalDateTime entryDate;

    @Column(name = "enterby", nullable = false, length = 50)
    private String enterBy;

    @Column(name = "transtatus", nullable = false)
    private Integer tranStatus;

    @Column(name = "pstatus", nullable = false)
    private Integer pStatus;

    @Column(name = "ptranid", length = 100)
    private String pTranId;

    @Column(name = "paymentdate")
    private LocalDateTime paymentDate;

    @Column(name = "paymentdateint", nullable = false)
    private Integer paymentDateInt;

    @Column(name = "pmonth")
    private Integer pMonth;

    @Column(name = "paymentby", length = 50)
    private String paymentBy;

    @Column(name = "approveruser1", length = 50)
    private String approverUser1;

    @Column(name = "approveruser2", length = 50)
    private String approverUser2;

    @Column(name = "approveddate1")
    private LocalDateTime approvedDate1;

    @Column(name = "approveddate2")
    private LocalDateTime approvedDate2;

    @Column(name = "approveddate2int", nullable = false)
    private Integer approvedDate2Int;

    @Column(name = "treasury", nullable = false)
    private Integer treasury;

    @Column(name = "StatusMessage", length = 500)
    private String statusMessage;

    @Column(name = "categoryPurpose")
    private Integer categoryPurpose;

    @Column(name = "paymentreverse", nullable = false)
    private Integer paymentReverse;

    @Column(name = "successSN", nullable = false)
    private Long successSN;

    @Column(name = "tag", nullable = false)
    private Long tag;

    @Column(name = "kattirefid", nullable = false)
    private Long kattiRefId;

    @Column(name = "ebpcode", length = 50)
    private String ebpCode;

    @Column(name = "rkattiid", nullable = false)
    private Long rKattiId;

    @Column(name = "parentcode", nullable = false)
    private Long parentCode;


    @Override
    public String toString() {
        return "{" +
                "\"eftNo\":" + eftNo + "," +
                "\"fyId\":" + fyId + "," +
                "\"adminId\":" + adminId + "," +
                "\"orgId\":" + orgId + "," +
                "\"journalId\":" + journalId + "," +
                "\"senderBankCode\":" + senderBankCode + "," +
                "\"senderBankName\":\"" + senderBankName + "\"," +
                "\"senderBranchCode\":" + senderBranchCode + "," +
                "\"senderBranch\":\"" + senderBranch + "\"," +
                "\"senderAccountNo\":\"" + senderAccountNo + "\"," +
                "\"senderAccountName\":\"" + senderAccountName + "\"," +
                "\"receiverType\":" + receiverType + "," +
                "\"receiverId\":" + receiverId + "," +
                "\"receiverBankCode\":" + receiverBankCode + "," +
                "\"receiverBankId\":" + receiverBankId + "," +
                "\"receiverBank\":\"" + receiverBank + "\"," +
                "\"receiverBranchCode\":" + receiverBranchCode + "," +
                "\"receiverBranch\":\"" + receiverBranch + "\"," +
                "\"receiverAccountNo\":\"" + receiverAccountNo + "\"," +
                "\"receiverName\":\"" + receiverName + "\"," +
                "\"tAmount\":" + tAmount + "," +
                "\"entryDate\":\"" + entryDate + "\"," +
                "\"enterBy\":\"" + enterBy + "\"," +
                "\"tranStatus\":" + tranStatus + "," +
                "\"pStatus\":" + pStatus + "," +
                "\"pTranId\":\"" + pTranId + "\"," +
                "\"paymentDate\":\"" + paymentDate + "\"," +
                "\"paymentDateInt\":" + paymentDateInt + "," +
                "\"pMonth\":" + pMonth + "," +
                "\"paymentBy\":\"" + paymentBy + "\"," +
                "\"approverUser1\":\"" + approverUser1 + "\"," +
                "\"approverUser2\":\"" + approverUser2 + "\"," +
                "\"approvedDate1\":\"" + approvedDate1 + "\"," +
                "\"approvedDate2\":\"" + approvedDate2 + "\"," +
                "\"approvedDate2Int\":" + approvedDate2Int + "," +
                "\"treasury\":" + treasury + "," +
                "\"statusMessage\":\"" + statusMessage + "\"," +
                "\"categoryPurpose\":" + categoryPurpose + "," +
                "\"paymentReverse\":" + paymentReverse + "," +
                "\"successSN\":" + successSN + "," +
                "\"tag\":" + tag + "," +
                "\"kattiRefId\":" + kattiRefId + "," +
                "\"ebpCode\":\"" + ebpCode + "\"," +
                "\"rKattiId\":" + rKattiId + "," +
                "\"parentCode\":" + parentCode + "," +
                           "}";
    }

}

