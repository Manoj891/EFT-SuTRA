package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountBalanceRes {
    private String responseCode;
    private String bankId;
    private String branchId;
    private String accountId;
    private String currency;
    private Double totalBal;
    private Double availBal;
    private String abPartTranType;
    private String lbPartTranType;
    private String responseMessage;
    private List<Object> classfielderrorlist;
}
