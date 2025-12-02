package com.fcgo.eft.sutra.util;

import com.fcgo.eft.sutra.configure.StringToJsonNode;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.repository.BankHeadOfficeRepository;
import com.fcgo.eft.sutra.repository.EftNchlRbbBankMappingRepository;
import com.fcgo.eft.sutra.repository.NchlReconciledRepository;
import com.fcgo.eft.sutra.service.*;
import com.fcgo.eft.sutra.service.impl.PoCodeMappedService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeCheckStatusService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCheckStatus {
    private final NonRealTimeCheckStatusService nonRealTime;
    private final RealTimeCheckStatusService realTime;
    private final NchlReconciledRepository repository;
    private final TransactionStatusUpdate statusUpdate;
    private final BankAccountDetailsService bankAccountDetailsService;
    private final BankHeadOfficeService bankHeadOfficeService;
    private final BankHeadOfficeRepository headOfficeRepository;
    private final EftNchlRbbBankMappingRepository eftNchlRbbBankMappingRepository;
    private final PaymentReceiveService bankMapService;
    private final EftPaymentReceiveService paymentReceiveService;
    private final IsProdService isProdService;
    private final ThreadPoolExecutor executor;
    private final LoginService loginService;
    private final StringToJsonNode jsonNode;
    private final PoCodeMappedService poCodeMappedService;
    @Value("${server.port}")
    private String port;


    @PostConstruct
    public void executePostConstruct() {
        Workbook workbookWrite = new XSSFWorkbook();
        Sheet sheetWrite = workbookWrite.createSheet("Sheet1");
        Row rowWrite = sheetWrite.createRow(0);
        // Create cells and set values
        rowWrite.createCell(0).setCellValue("INSTRUCTION ID");
        rowWrite.createCell(1).setCellValue("CREDIT STATUS");
        rowWrite.createCell(2).setCellValue("DEBIT STATUS");
        FileInputStream fis = null;
        String filePath = "C:/Users/Manoj/Documents/ResendFailureEFT.xlsx";
        try {
            fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
           AtomicInteger count= new AtomicInteger(1);
            for (Row row : sheet) {
                try {
                    long eftNo = Long.parseLong(row.getCell(1).toString().replace("\"", ""));
                    repository.findById(eftNo).ifPresent(e -> {
                        if (e.getCreditStatus().equals("000") || e.getCreditStatus().equals("ACSC")) {
                            Row newRow = sheetWrite.createRow(count.get());
                            // Create cells and set values
                            newRow.createCell(0).setCellValue("'"+eftNo);
                            newRow.createCell(1).setCellValue("'"+ e.getCreditStatus());
                            newRow.createCell(2).setCellValue("'"+e.getDebitStatus());
                            count.getAndIncrement();
                            System.out.println(eftNo + " " + e.getCreditStatus() + " " + e.getDebitStatus());
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }


            }
            try (FileOutputStream fos = new FileOutputStream("C:/Users/Manoj/Documents/ResendSuccess.xlsx")) {
                workbookWrite.write(fos);
                workbookWrite.close();
                System.out.println("Excel file created successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("----------------------------------------------------");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            fis.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        statusUpdate.init();
        poCodeMappedService.setDate();
        bankHeadOfficeService.setHeadOfficeId();
        bankMapService.setBankMaps(eftNchlRbbBankMappingRepository.findBankMap());
        isProdService.init();
        loginService.init();
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void updateStatus() {
        if (!statusUpdate.isStarted()) {
            statusUpdate.statusUpdateApi();
        }
    }


    @Scheduled(cron = "0 30 08,09,10,11,12,13,14,15,16,17,18,20,22 * * *")
    public void executeEveryHour30Min() {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            long startTime = 20251018000000L;
            long dateTime = Long.parseLong(jsonNode.getYyyyMMddHHmmss().format(new Date())) - (12000);

            headOfficeRepository.updatePaymentPendingStatusDetail(startTime, dateTime);
            headOfficeRepository.updatePaymentPendingStatusMaster(startTime, dateTime);
            headOfficeRepository.updatePaymentPendingStatusDetail();
            repository.updateMissingStatusSent();
            executor.submit(() -> paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build()));
        }
    }

    @Scheduled(cron = "0 15 08,12,16,20,22 * * *")
    public void executeStatus() {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            executor.submit(() -> {
                repository.findRealTimePendingInstructionId().forEach(instructionId -> {
                    realTime.checkStatusByInstructionId(instructionId, 0);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                });

                repository.findNonRealTimePendingBatchId().forEach(batchId -> {
                    nonRealTime.checkStatusByBatchId(batchId);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                });
//                tryForNextAttempt();
                repository.updateMissingStatusSent();

            });
        }
    }

    @Scheduled(cron = "0 05 00 * * *")
    public void executeCheckTransactionStatus() {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -1);
            String date = jsonNode.getDateFormat().format(calendar.getTime());
            log.info("{} Non Real Time Status", date);
            nonRealTime.checkStatusByDate(date);
            log.info("Non Real Time Status Completed {} Real Time Status", date);
            realTime.checkStatusByDate(date);
            log.info("Non Real Time Status Completed {}", date);
            repository.updateMissingStatusSent();
//            tryForNextAttempt();
        }
    }

    @Scheduled(cron = "0 01 02 * * *")
    public void fetchBankAccountDetails() {
        if (isProdService.isProdService() && port.equalsIgnoreCase("7891")) {
            bankAccountDetailsService.fetchBankAccountDetails();
        }
    }

//    private void tryForNextAttempt() {
//        repository.missingStatusSent();
//        repository.findTryForNextAttempt().forEach(id -> {
//            repository.missingStatusSent(id);
//            log.info("{} Trying For Next Attempt", id);
//        });
//    }

//    public void tryTimeOutToReject() {
//        repository.findTryTimeOutToReject().forEach(m -> {
//            long instructionId = Long.parseLong(m.get("INSTRUCTION_ID").toString());
//            String message = m.get("CREDIT_MESSAGE").toString();
//            message = (message.substring(0, message.indexOf(". We will try again"))) + " Reject after " + m.get("TRY_COUNT") + " times on " + m.get("TRY_TIME") + ".";
//            repository.updateRejectTransaction("1000", message, "997", "Reject", instructionId);
//            log.info("Reject Transaction: {}", instructionId);
//        });
//    }
}
