package com.fcgo.eft.sutra.dto;

import com.fcgo.eft.sutra.dto.res.NchlIpsBatchDetailRes;
import com.fcgo.eft.sutra.dto.res.NchlIpsTransactionDetail;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCipsByDateResponseWrapper {
    private NchlIpsBatchDetailRes nchlIpsBatchDetail;
    private List<NchlIpsTransactionDetail> nchlIpsTransactionDetailList;
}
