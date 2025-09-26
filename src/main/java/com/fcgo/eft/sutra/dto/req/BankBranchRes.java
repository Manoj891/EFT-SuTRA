package com.fcgo.eft.sutra.dto.req;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankBranchRes {
    private String branchId;
    private String bankId;
    private String branchName;
}
