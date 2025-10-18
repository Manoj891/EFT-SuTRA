package com.fcgo.eft.sutra.service.nonrealtime;

public interface NonRealTimeCheckStatusService {
    void checkStatusByDate(String date);

    void checkStatusByBatchId(String batchId) ;
}
