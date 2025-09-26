package com.fcgo.eft.sutra.token;


import com.fcgo.eft.sutra.dto.req.CipsFundTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenGenerate {
    private final MessageSigning messageSigning;

    public String generateHash(CipsFundTransfer transactionRequest) {

        String batchString = transactionRequest.getNchlIpsBatchDetail().getBatchId() + "," + transactionRequest.getNchlIpsBatchDetail().getDebtorAgent()
                + "," + transactionRequest.getNchlIpsBatchDetail().getDebtorBranch() + "," + transactionRequest.getNchlIpsBatchDetail().getDebtorAccount()
                + "," + transactionRequest.getNchlIpsBatchDetail().getBatchAmount() + "," + "NPR," + transactionRequest.getNchlIpsBatchDetail().getCategoryPurpose() + ",";


        StringBuffer stringBuffer = new StringBuffer();
        String transStringnew;
        for (int i = 0; i < transactionRequest.getNchlIpsTransactionDetailList().size(); i++) {
            transStringnew = transactionRequest.getNchlIpsTransactionDetailList().get(i).getInstructionId() + "," + transactionRequest.getNchlIpsTransactionDetailList().get(i).getCreditorAgent() + ","
                    + transactionRequest.getNchlIpsTransactionDetailList().get(i).getCreditorBranch() + "," + transactionRequest.getNchlIpsTransactionDetailList().get(i).getCreditorAccount()
                    + "," + transactionRequest.getNchlIpsTransactionDetailList().get(i).getAmount() + ",";
            stringBuffer.append(transStringnew);
        }
        String tokenString = batchString + stringBuffer + "FCGOSUTRA@999";
        return messageSigning.getHashValue(tokenString);

    }

    public String geterateHashCipsBatch(String instructionId, String debtorBranch, String debtorAgent, String debtorAccount, String creditorAccount, String creditorAgent, String creditorBranch, String amount) {
        return messageSigning.getHashValue(instructionId + "," + debtorAgent + "," + debtorBranch + "," + debtorAccount + "," + amount + "," + "NPR" + "," + instructionId + "," + creditorAgent + "," + creditorBranch + "," + creditorAccount + "," + amount + "," + "FCGOSUTRA@999");

    }
}
