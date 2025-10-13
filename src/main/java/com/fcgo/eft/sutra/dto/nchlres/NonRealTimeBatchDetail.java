package com.fcgo.eft.sutra.dto.nchlres;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NonRealTimeBatchDetail {
    private String id;
    private String batchId;
    private Date recDate;
    private Long instructionId;
    private String endToEndId;
    private String amount;
    private String chargeAmount;
    private String creditorAgent;
    private Integer creditorBranch;
    private String creditorName;
    private String creditorAccount;
    private String creditStatus;
    private String reasonCode;
    private String reversalStatus;
    @Getter(AccessLevel.NONE)
    private String reasonDesc;
    private String txnResponse;
    private String ipsBatchId;

   ;private String addenda1   ;private String addenda2   ;private String addenda3   ;private String addenda4   ;


    /*
    "id":71657926, "batchId":15282003, "isoTxnId":null, "recDate":"2025-10-12", "instructionId":
            "86600382830001053", "endToEndId":"SUTRA/SHARADA Municipality, ", "amount":60000.00, "chargeAmount":
            0.00, "chargeLiability":"CG", "purpose":null, "merchantId":null, "appId":"MER-1-APP-3", "appTxnId":
            null, "creditorAgent":"2501", "creditorBranch":"9", "creditorName":
            "MISSION PUBLIC MEDIA PVT.LTD", "creditorAccount":"0330065734000011", "addenda1":
            1760235563898, "addenda2":"2025-10-12", "addenda3":"86600382830001053", "addenda4":
            "SHARADA Municipality, Salyan", "creditStatus":"ACTC", "reasonCode":null, "reversalStatus":null, "refId":
            "86600382830001053", "remarks":"86600382830001053", "particulars":null, "freeCode1":null, "freeCode2":
            null, "freeText1":null, "freeText2":null, "freeText3":null, "freeText4":null, "freeText5":null, "freeText6":
            null, "freeText7":null, "beneficiaryId":null, "beneficiaryName":null, "ipsBatchId":"0101251012ASA", "rcreUserId":
            "FCGOSUTRA@999", "rcreTime":"2025-10-12T02:19:25.457+0000", "ipsTxnId":"0101251012ASA00B", "reasonDesc":
            null, "txnResponse":null
*/
    public String getReasonDesc() {
        return reasonDesc == null ? "" : reasonDesc;
    }
}
