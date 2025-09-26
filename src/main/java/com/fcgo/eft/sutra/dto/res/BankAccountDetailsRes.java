package com.fcgo.eft.sutra.dto.res;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountDetailsRes {
    private String timestamp;
    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private List<BankAccountDetails> data;
}
