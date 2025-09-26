package com.fcgo.eft.sutra.dto.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountDetails {
    private String entryId;
    private String bankId;
    private String branchId;
    private String accountName;
    private String accountId;
    private String bankName;
    private String status;
    private String rcreTime;

    @Override
    public String toString() {
        return "{\"entryId\":\"" + entryId + "\",\"bankId\":\"" + bankId + "\",\"branchId\":\"" + branchId + "\",\"accountName\":\"" + accountName + "\",\"accountId\":\"" + accountId + "\",\"bankName\":\"" + bankName + "\",\"status\":\"" + status + "\",\"rcreTime\":\"" + rcreTime + "\"}";
    }
}
