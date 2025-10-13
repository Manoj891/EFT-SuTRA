package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NchlIpsTransactionDetail {
    private String id;
    private String batchId;
    private Date recDate;
    private long instructionId;
    private String endToEndId;
    private double amount;
    private double chargeAmount;
    private String chargeLiability;
    private String creditorAgent;
    private String creditorBranch;
    private String creditorName;
    private String creditorAccount;
    private long addenda1;
    private String addenda2;
    private String addenda3;
    private String addenda4;
    private String purpose;
    private String creditStatus;
    private String refId;
    private String remarks;
    private String ipsBatchId;
    private String rcreTime;
    private String reasonDesc;
    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"batchId\":\"" + batchId + "\"," +
                "\"recDate\":\"" + recDate + "\"," +
                "\"instructionId\":\"" + instructionId + "\"," +
                "\"endToEndId\":\"" + endToEndId + "\"," +
                "\"amount\":\"" + amount + "\"," +
                "\"chargeAmount\":\"" + chargeAmount + "\"," +
                "\"chargeLiability\":\"" + chargeLiability + "\"," +
                "\"creditorAgent\":\"" + creditorAgent + "\"," +
                "\"creditorBranch\":\"" + creditorBranch + "\"," +
                "\"creditorName\":\"" + creditorName + "\"," +
                "\"creditorAccount\":\"" + creditorAccount + "\"," +
                "\"addenda1\":\"" + addenda1 + "\"," +
                "\"addenda2\":\"" + addenda2 + "\"," +
                "\"addenda3\":\"" + addenda3 + "\"," +
                "\"addenda4\":\"" + addenda4 + "\"," +
                "\"creditStatus\":\"" + creditStatus + "\"," +
                "\"refId\":\"" + refId + "\"," +
                "\"remarks\":\"" + remarks + "\"," +
                "\"ipsBatchId\":\"" + ipsBatchId + "\"," +
                "\"rcreTime\":\"" + rcreTime + "\"," +
                "\"reasonDesc\":\"" + reasonDesc + "\"" +
                "}";
    }


}
