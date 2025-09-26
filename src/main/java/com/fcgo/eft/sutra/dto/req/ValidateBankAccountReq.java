package com.fcgo.eft.sutra.dto.req;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateBankAccountReq {
    private String bankId;
    private String accountId;
    private String accountName;
}
