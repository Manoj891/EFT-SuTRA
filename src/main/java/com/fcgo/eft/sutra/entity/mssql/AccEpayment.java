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


    @Column(name = "pstatus", nullable = false)
    private Integer pStatus;


    @Column(name = "StatusMessage", length = 500)
    private String statusMessage;


    @Column(name = "successSN", nullable = false)
    private Long successSn;



    @Column(nullable = false)
    private Long adminid = 0L;

    @Column(nullable = false)
    private Long orgid = 0L;


    @Column(nullable = false)
    private Long journalid = 0L;

    @Column(nullable = false)
    private LocalDateTime trandate = LocalDateTime.now();




    @Column(length = 100, nullable = false)
    private String senderbankname = "";





    @Column(length = 50, nullable = false)
    private String senderaccountno;

    @Column(length = 150, nullable = false)
    private String senderaccountname;

    @Column(nullable = false)
    private Long receivertype;

    @Column(nullable = false)
    private Long receiverid;

    @Column(nullable = false)
    private Long receiverbankcode = 0L;

    @Column(nullable = false)
    private Long receiverbankid = 0L;

    @Column(length = 100, nullable = false)
    private String receiverbank;

    @Column(nullable = false)
    private Long receiverbranchcode = 0L;

    @Column(length = 50, nullable = false)
    private String receiverbranch = "";

    @Column(length = 50, nullable = false)
    private String receiveraccountno;

    @Column(length = 150, nullable = false)
    private String receivername;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal tamount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime entrydate ;

    @Column(length = 50, nullable = false)
    private String enterby;

    @Column(nullable = false)
    private Integer transtatus = 0;

    @Column(nullable = false)
    private Integer pstatus = 0;

    @Column(length = 100)
    private String ptranid;

    private LocalDateTime paymentdate;

    @Column(nullable = false)
    private Integer paymentdateint = 0;

    private Integer pmonth;

    @Column(length = 50)
    private String paymentby;

    @Column(length = 50)
    private String approveruser1;

    @Column(length = 50)
    private String approveruser2;


    @Column(nullable = false)
    private Integer treasury = 1;


    @Column(nullable = false)
    private Long successSN = 0L;


    @Override
    public String toString() {
        return "{" +
                "eftNo=" + eftNo +
                ", pStatus=" + pStatus +
                ", statusMessage='" + statusMessage + '\'' +
                ", successSn=" + successSn +
                ", adminid=" + adminid +
                ", orgid=" + orgid +
                ", journalid=" + journalid +
                ", trandate=" + trandate +
                ", senderbankname='" + senderbankname + '\'' +
                ", senderaccountno='" + senderaccountno + '\'' +
                ", senderaccountname='" + senderaccountname + '\'' +
                ", receivertype=" + receivertype +
                ", receiverid=" + receiverid +
                ", receiverbankcode=" + receiverbankcode +
                ", receiverbankid=" + receiverbankid +
                ", receiverbank='" + receiverbank + '\'' +
                ", receiverbranchcode=" + receiverbranchcode +
                ", receiverbranch='" + receiverbranch + '\'' +
                ", receiveraccountno='" + receiveraccountno + '\'' +
                ", receivername='" + receivername + '\'' +
                ", tamount=" + tamount +
                ", entrydate=" + entrydate +
                ", enterby='" + enterby + '\'' +
                ", transtatus=" + transtatus +
                ", pstatus=" + pstatus +
                ", ptranid='" + ptranid + '\'' +
                ", paymentdate=" + paymentdate +
                ", paymentdateint=" + paymentdateint +
                ", pmonth=" + pmonth +
                ", paymentby='" + paymentby + '\'' +
                ", approveruser1='" + approveruser1 + '\'' +
                ", approveruser2='" + approveruser2 + '\'' +
                ", treasury=" + treasury +
                ", successSN=" + successSN +
                '}';
    }
}
