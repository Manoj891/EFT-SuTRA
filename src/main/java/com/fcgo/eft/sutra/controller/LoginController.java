package com.fcgo.eft.sutra.controller;


import com.fcgo.eft.sutra.dto.req.LoginReq;
import com.fcgo.eft.sutra.dto.res.LoginRes;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.service.BankAccountDetailsService;
import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import com.fcgo.eft.sutra.service.LoginService;
import com.fcgo.eft.sutra.service.RealTimeCheckStatusService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeStatusFromNchl;
import com.fcgo.eft.sutra.util.IsProdService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/api/")
public class LoginController {
    private final LoginService service;
    private final EftPaymentReceiveService paymentReceiveService;
    private final NonRealTimeStatusFromNchl statusFromNchl;
    private final RealTimeCheckStatusService realTimeCheckStatusService;
    private final BankAccountDetailsService bankAccountDetailsService;
    private final IsProdService isProdService;

    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq req, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.login(req, request));
    }

    @GetMapping("/payment-start")
    public ResponseEntity<Void> paymentStart() {
        paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/bank-account/details")
    public ResponseEntity<Void> bankAccountDetails() {
        bankAccountDetailsService.fetchBankAccountDetails();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/nonrealtime/{batchId}")
    public ResponseEntity<Object> nonRealTransaction(@PathVariable String batchId) {
        return ResponseEntity.status(HttpStatus.OK).body(statusFromNchl.checkByBatchId(batchId));
    }

    @GetMapping("/realtime/{instructionId}")
    public ResponseEntity<List<Object>> realTransaction(@PathVariable String instructionId) {
        List<Object> resp = new ArrayList<>();
        for (String s : instructionId.split(",")) {
            try {
                if (s.length() > 5) {
                    resp.add(realTimeCheckStatusService.checkStatusByInstructionId(s, 0));
                }
            } catch (Exception ex) {
                resp.add(s + " " + ex.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/transaction")
    public ResponseEntity<String> realTransaction(@RequestParam boolean status) {
        isProdService.setStarted(status);
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
