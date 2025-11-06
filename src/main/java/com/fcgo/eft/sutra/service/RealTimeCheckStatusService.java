package com.fcgo.eft.sutra.service;

public interface RealTimeCheckStatusService {
    void checkStatusByDate(String date);

    void checkStatusByInstructionId(String instructionId);
    Object getRealTimeByBatch(String instructionId);
}
