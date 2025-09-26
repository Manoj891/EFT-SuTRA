package com.fcgo.eft.sutra.dto.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NchlIpsTransactionDetailList {
    private Long id;
    private Long batchId;
    private String recDate;
    private String instructionId;
    private String endToEndId;
    private Double amount;
    private Double chargeAmount;
    private String chargeLiability;
    private String purpose;
    private String merchantId;
    private String creditorAgent;
    private String creditorBranch;
    private String creditorName;
    private String creditorAccount;

    @Override
    public String toString() {
        return "{\"id\":\"" + id +
                ", batchId\":\"" + batchId +
                "\",\"recDate\":\"" + recDate +
                "\",\"instructionId\":\"" + instructionId +
                "\",\"endToEndId\":\"" + endToEndId +
                "\",\"amount\":\"" + amount +
                "\",\"chargeAmount\":\"" + chargeAmount +
                "\",\"chargeLiability\":\"" + chargeLiability +
                "\",\"purpose\":\"" + purpose +
                "\",\"merchantId\":\"" + merchantId +
                "\",\"creditorAgent\":\"" + creditorAgent +
                "\",\"creditorBranch\":\"" + creditorBranch +
                "\",\"creditorName\":\"" + creditorName +
                "\",\"creditorAccount\":\"" + creditorAccount + "\"}";
    }
}
