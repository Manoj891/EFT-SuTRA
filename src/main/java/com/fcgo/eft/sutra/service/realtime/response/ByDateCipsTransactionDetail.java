package com.fcgo.eft.sutra.service.realtime.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ByDateCipsTransactionDetail {
    private Long id;
    private Date recDate;
    private Long instructionId;
    private String endToEndId;
    private String chargeLiability;
    private String purpose;
    private String creditStatus;
    private String reasonCode;
    private String remarks;
    private String particulars;
    private String reasonDesc;
}
