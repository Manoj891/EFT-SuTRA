package com.fcgo.eft.sutra.service;

public interface RealTimeCheckStatusService {
    void checkStatusByDate(String date);

    Object checkStatusByInstructionId(String instructionId);

    void convert(String res, long time);
}
