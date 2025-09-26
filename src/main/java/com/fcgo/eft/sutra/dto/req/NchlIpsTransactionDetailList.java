package com.fcgo.eft.sutra.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NchlIpsTransactionDetailList {
    private String instructionId;
    private String endToEndId;
    private BigDecimal amount;
    @JsonIgnore
    private String purpose;
    private String creditorAgent;
    private String creditorBranch;
    private String creditorName;
    private String creditorAccount;
    //to provide extra information abt the transaction..
    private long addenda1;
    private String addenda2;
    private String addenda3;
    private String addenda4;
    //to identify from where the transaction is being initiated..
    private String channelId;
    //extra info abt transaction to be more specific..
    private String refId;
    private String remarks;

    @Override
    public String toString() {
        return "{\"instructionId\":\"" + instructionId + "\",\"endToEndId\":\"" + endToEndId + "\",\"amount=" + amount + "\",\"purpose\":\"" + purpose + "\",\"creditorAgent\":\"" + creditorAgent + "\",\"creditorBranch\":\"" + creditorBranch + "\",\"creditorName\":\"" + creditorName + "\",\"creditorAccount\":\"" + creditorAccount + "\",\"addenda1=" + addenda1 + "\",\"addenda2=" + addenda2 + "\",\"addenda3\":\"" + addenda3 + "\",\"addenda4\":\"" + addenda4 + "\",\"channelId\":\"" + channelId + "\",\"refId\":\"" + refId + "\",\"remarks\":\"" + remarks + "\"}";

    }
}
