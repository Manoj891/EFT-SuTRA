package com.fcgo.eft.sutra.test;

import com.fcgo.eft.sutra.repository.mssql.AccEpaymentRepository;
import com.fcgo.eft.sutra.repository.oracle.TestRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    private final AccEpaymentRepository accEpaymentRepository;

    @PostConstruct
    public void init() {
        try {

            FileWriter writer = new FileWriter("D:/home/tomcat/duplicate.csv");
            writer.write("RECONCILED_DATE,BATCH_ID,INSTRUCTION_ID,CREDIT_STATUS,DEBTOR_ACCOUNT,DEBTOR_NAME,CREDITOR_ACCOUNT,CREDITOR_NAME,END_TO_END_ID,ADDENDA4,AMOUNT\n");
            for (long eftNumber : accEpaymentRepository.eftNumberRejected()) {
                Map<String, Object> map = testRepository.findNchlTransactionSuccess(eftNumber);
                if (map != null && map.size() > 2) {
                    String ststus = map.get("CREDIT_STATUS").toString();
                    if (ststus.equals("000") || ststus.equals("ACSC")) {
                        String ADDENDA4 = map.get("ADDENDA4").toString().replace(",", "");
                        String END_TO_END_ID = map.get("END_TO_END_ID").toString().replace(",", "");
                        try {
                            writer.write("'" + map.get("RECONCILED_DATE") + "," + (ststus.equals("000") ? "'" + eftNumber : map.get("BATCH_ID")) + ",'" + eftNumber + ",'" + ststus + ",'" + map.get("DEBTOR_ACCOUNT")
                                    + "," + map.get("DEBTOR_NAME") + ",'" + map.get("CREDITOR_ACCOUNT") + "," + map.get("CREDITOR_NAME")
                                    + "," + END_TO_END_ID + ",'" + ADDENDA4 + ",'" + map.get("AMOUNT") + "\n");
                            System.out.println("Writed " + eftNumber);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            ;
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        List<Long> longs =
//
//        longs.forEach(eftNumber -> {
//            Optional<AccEpaymentRes> accEpaymentRes = accEpaymentRepository.findByEftNumber(eftNumber);
//            if(!accEpaymentRes.isPresent()) {
//                System.out.println(eftNumber+" Not present");
//            }else {
//                System.out.println(eftNumber+" is present");
//            }
//        });
    }
}
