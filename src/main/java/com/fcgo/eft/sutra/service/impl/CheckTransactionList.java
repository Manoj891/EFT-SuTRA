package com.fcgo.eft.sutra.service.impl;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.dto.EftStatus;
import com.fcgo.eft.sutra.dto.res.EftPaymentRequestDetailProjection;
import com.fcgo.eft.sutra.entity.oracle.NchlReconciled;
import com.fcgo.eft.sutra.repository.oracle.NchlReconciledRepository;
import com.fcgo.eft.sutra.util.DB2nd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckTransactionList {
    private final NchlReconciledRepository reconciledRepository;
    private final StringToJsonNode jsonNode;
    private final DB2nd db2nd;


    public synchronized List<EftPaymentRequestDetailProjection> getList(List<EftPaymentRequestDetailProjection> l1, List<EftPaymentRequestDetailProjection> l2) {
        List<EftPaymentRequestDetailProjection> list = new ArrayList<>();
        try {
            l1.forEach(d -> {
                long eftNo = Long.parseLong(d.getInstructionId());
                try {
                    EftStatus status = db2nd.getRecord(d.getInstructionId());
                    if (status != null) {
                        if (status.getPstatus() == 2) {
                            EftPaymentRequestDetailProjection obj = getData(status, d, eftNo);
                            if (obj != null) {
                                list.add(obj);
                            }
                        } else if (status.getPstatus() == -1) {
                            reject(eftNo);
                        }
                    } else {
                        reject(eftNo);
                    }
                } catch (Exception ignored) {
                }
            });

            l2.forEach(d -> {
                long eftNo = Long.parseLong(d.getInstructionId());
                try {
                    EftStatus status = db2nd.getRecord(d.getInstructionId());
                    if (status != null) {
                        if (status.getPstatus() == 2) {
                            EftPaymentRequestDetailProjection obj = getData(status, d, eftNo);
                            if (obj != null) {
                                list.add(obj);
                            }
                        } else if (status.getPstatus() == -1) {
                            reject(eftNo);
                        }
                    } else {
                        reject(eftNo);
                    }
                } catch (Exception ignored) {
                }
            });

            return list;
        } catch (Exception ignored) {
        }
        return list;
    }

    private EftPaymentRequestDetailProjection getData(EftStatus status, EftPaymentRequestDetailProjection obj, long eftNo) {
        if (status.getPstatus() == 2 && status.getTranstatus() == 2) {
            return obj;
        } else if (status.getPstatus() == -1) {
            reject(eftNo);
        } else if (status.getPstatus() == 1) {
            long datetime=Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date()));
            reconciledRepository.save(NchlReconciled.builder().instructionId(eftNo).
                    debitStatus("000").
                    debitMessage("-").
                    creditStatus("000").
                    creditMessage("Success").
                    recDate(new Date()).
                    pushed("N").
                    transactionId("NA").
                    updatedAt(LocalDateTime.now())
                    .pushedDatetime(datetime)
                    .build());
            reconciledRepository.updateManualReject(datetime,String.valueOf(eftNo));
        }
        return null;
    }

    private void reject(long instructionId) {
        Optional<NchlReconciled> optional = reconciledRepository.findById(instructionId);
        if (optional.isEmpty()) {
            reconciledRepository.save(NchlReconciled.builder().instructionId(instructionId).
                    debitStatus("097").
                    debitMessage("Not Found").
                    creditStatus("-1000").
                    creditMessage("Record Not found, Please Conform with bank before new transaction initialized").
                    recDate(new Date()).
                    pushed("N").
                    transactionId("NA").
                    updatedAt(LocalDateTime.now())
                    .pushedDatetime(Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date())))
                    .build());
            reconciledRepository.updateManualReject(String.valueOf(instructionId));
        } else {
            NchlReconciled reconciled = optional.get();
            if (reconciled.getCreditStatus().equals("SENT")) {
                reconciled.setCreditStatus("-1000");
                reconciled.setCreditMessage("Record Not found, Please Conform with bank before new transaction initialized");
                reconciled.setDebitStatus("097");
                reconciled.setDebitMessage("Not Found");
                reconciled.setPushedDatetime(Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date())));
                reconciledRepository.save(reconciled);
                reconciledRepository.updateManualReject(Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date())), String.valueOf(instructionId));
            }
        }
    }
}
