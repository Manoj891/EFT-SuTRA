package com.fcgo.eft.sutra.dto.req;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EftPaymentRequestDetailReq {
    private String instructionId;
    private String endToEndId;
    private BigDecimal amount;
    private String creditorAgent;
    private String creditorBranch;
    private String creditorName;
    private String creditorAccount;
    private String addenda1;
    private String addenda2;
    private String addenda3;
    private String addenda4;
    private String refId;
    private String remarks;
    private String particulars;

    @Override
    public String toString() {
        return "{\"instructionId\":\"" + instructionId +
                "\",\"endToEndId\":\"" + endToEndId +
                "\",\"amount\":\"" + amount +
                "\",\"creditorAgent\":\"" + creditorAgent +
                "\",\"creditorBranch\":\"" + creditorBranch +
                "\",\"creditorName\":\"" + creditorName +
                "\",\"creditorAccount\":\"" + creditorAccount +
                "\",\"addenda1\":\"" + addenda1 +
                "\",\"addenda2\":\"" + addenda2 +
                "\",\"addenda3\":\"" + addenda3 +
                "\",\"addenda4\":\"" + addenda4 +
                "\",\"refId\":\"" + refId +
                "\",\"remarks\":\"" + remarks +
                "\",\"particulars\":\"" + particulars + "\"}\n";
    }
}
