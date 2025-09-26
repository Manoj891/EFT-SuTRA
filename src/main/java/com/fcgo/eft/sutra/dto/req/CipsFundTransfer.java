package com.fcgo.eft.sutra.dto.req;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CipsFundTransfer {
    private NchlIpsBatchDetail nchlIpsBatchDetail;
    private List<NchlIpsTransactionDetailList> nchlIpsTransactionDetailList;
    private String token;

    @Override
    public String toString() {
        return "{\"nchlIpsBatchDetail\":\"" + nchlIpsBatchDetail + "\",\"nchlIpsTransactionDetailList\":\"" + nchlIpsTransactionDetailList + "\"}";
    }
}
