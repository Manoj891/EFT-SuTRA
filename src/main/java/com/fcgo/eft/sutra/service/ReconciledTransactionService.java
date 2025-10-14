package com.fcgo.eft.sutra.service;

import com.fcgo.eft.sutra.dto.res.NchlIpsBatchDetailRes;
import com.fcgo.eft.sutra.dto.res.NchlIpsTransactionDetail;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransaction;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransactionDetail;

import java.util.List;

public interface ReconciledTransactionService {
    void save(RealTimeTransaction rtt, RealTimeTransactionDetail detail, long time);
    void save(NchlIpsBatchDetailRes batch,List<NchlIpsTransactionDetail> details, long time);

}
