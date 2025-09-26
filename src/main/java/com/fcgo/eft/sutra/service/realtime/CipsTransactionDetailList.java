package com.fcgo.eft.sutra.service.realtime;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CipsTransactionDetailList {
    private String instructionId;
    private String endToEndId;
    private BigDecimal amount;
    private String purpose;
    private String creditorAgent;
    private String creditorBranch;
    private String creditorName;
    private String creditorAccount;

    //to provide extra information abt the transaction..
    private int addenda1;
    private Date addenda2;
    private String addenda3;
    private String addenda4;
    //to identify from where the transaction is being initiated..
    private String channelId;
    //extra info abt transaction to be more specific..

    private String refId;
    private String remarks;
}
