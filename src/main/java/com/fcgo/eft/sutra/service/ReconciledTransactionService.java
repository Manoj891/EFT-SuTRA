package com.fcgo.eft.sutra.service;

import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransaction;
import com.fcgo.eft.sutra.service.realtime.response.RealTimeTransactionDetail;

public interface ReconciledTransactionService {
    void save(RealTimeTransaction rtt, RealTimeTransactionDetail detail, long time);
}
