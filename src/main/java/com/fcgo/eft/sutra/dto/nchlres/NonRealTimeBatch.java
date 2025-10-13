package com.fcgo.eft.sutra.dto.nchlres;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NonRealTimeBatch {
    private Long id;
    private String batchId;
    private Long isoTxnId;
    private String recDate;
    private Double batchAmount;
    private Integer batchCount;
    private String batchCrncy;
    private String categoryPurpose;
    private String debtorAgent;
    private String debtorBranch;
    private String debtorName;
    private String debtorAccount;
    private String debitStatus;
    private String debitReasonCode;
    private String settlementDate;
    private String debitReasonDesc;
    private String txnResponse;
   private List<NonRealTimeBatchDetail> nchlIpsTransactionDetailList;




    /*
        "nchlIpsBatchDetail":{
        "id":15282003, "batchId":"SU251012106701737962"
                , "recDate":"2025-10-12", "isoTxnId":678549, "batchAmount":60000.00,
                "batchCount":1, "batchChargeAmount":0.00, "batchCrncy":"NPR", "categoryPurpose":"GOVT", "debtorAgent":
        "0101", "debtorBranch":"74", "debtorName":"GA3.1 SHARADA NA.PA.DHARAUTI KHATA", "debtorAccount":
        "07403000003000000001", "debtorIdType":null, "debtorIdValue":null, "debtorAddress":null, "debtorPhone":
        null, "debtorMobile":null, "debtorEmail":null, "channelId":"TECHM", "debitStatus":"000", "debitReasonCode":
        null, "ipsBatchId":"0101251012ASA", "fileName":"251012_0101251012ASA.xml", "rcreTime":
        "2025-10-12T02:19:25.457+0000", "rcreUserId":"FCGOSUTRA@999", "sessionSeq":"53625", "settlementDate":
        "2025-10-12", "debitReasonDesc":"SUCCESS", "txnResponse":"", "nchlIpsTransactionDetailList":[{
            "id":71657926, "batchId":15282003, "isoTxnId":null, "recDate":"2025-10-12", "instructionId":
            "86600382830001053", "endToEndId":"SUTRA/SHARADA Municipality, ", "amount":60000.00, "chargeAmount":
            0.00, "chargeLiability":"CG", "purpose":null, "merchantId":null, "appId":"MER-1-APP-3", "appTxnId":
            null, "creditorAgent":"2501", "creditorBranch":"9", "creditorName":
            "MISSION PUBLIC MEDIA PVT.LTD", "creditorAccount":"0330065734000011", "creditorIdType":
            null, "creditorIdValue":null, "creditorAddress":null, "creditorPhone":null, "creditorMobile":
            null, "creditorEmail":null, "addenda1":1760235563898, "addenda2":"2025-10-12", "addenda3":
            "86600382830001053", "addenda4":"SHARADA Municipality, Salyan", "creditStatus":"ACTC", "reasonCode":
            null, "reversalStatus":null, "refId":"86600382830001053", "remarks":"86600382830001053", "particulars":
            null, "freeCode1":null, "freeCode2":null, "freeText1":null, "freeText2":null, "freeText3":null, "freeText4":
            null, "freeText5":null, "freeText6":null, "freeText7":null, "beneficiaryId":null, "beneficiaryName":
            null, "ipsBatchId":"0101251012ASA", "rcreUserId":"FCGOSUTRA@999", "rcreTime":
            "2025-10-12T02:19:25.457+0000", "ipsTxnId":"0101251012ASA00B", "reasonDesc":null, "txnResponse":null
        }]}
*/
}
