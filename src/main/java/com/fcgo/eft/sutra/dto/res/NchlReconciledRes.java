package com.fcgo.eft.sutra.dto.res;


public interface NchlReconciledRes {

    long getInstructionId();

    String getDebitStatus();

    String getDebitMessage();

    String getCreditStatus();

    String getCreditMessage();
}
