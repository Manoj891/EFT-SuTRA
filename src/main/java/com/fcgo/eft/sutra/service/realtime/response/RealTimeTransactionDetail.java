package com.fcgo.eft.sutra.service.realtime.response;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealTimeTransactionDetail {

    private String id;
    private Date recDate;
    private Long instructionId;
    private String endToEndId;
    private String chargeLiability;
    private String purpose;
    private String creditStatus;
    private String reasonCode;
    private String remarks;
    private String particulars;
    private String reasonDesc;
    private double amount;
    private double chargeAmount;
    private String creditorAgent;
    private String creditorBranch;
    private String creditorName;
    private String creditorAccount;
    private long addenda1;
    private String addenda2;
    private String addenda3;
    private String addenda4;
    private String refId;


}
