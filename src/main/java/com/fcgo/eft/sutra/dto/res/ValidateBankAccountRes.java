package com.fcgo.eft.sutra.dto.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateBankAccountRes {
    private String bankId;
    private String branchId;
    private String accountId;
    private String accountName;
    private String currency;
    private String responseCode;
    private String responseMessage;
    private String matchPercentate;

}
