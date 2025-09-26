package com.fcgo.eft.sutra.dto.req;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountBalanceReq {
    private String bankId;
    private String accountId;
    private String branchId;
}
