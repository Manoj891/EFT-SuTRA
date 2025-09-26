package com.fcgo.eft.sutra.controller;

import com.fcgo.eft.sutra.entity.oracle.BankAccountWhitelist;
import com.fcgo.eft.sutra.service.BankAccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BankAccountDetailsController {
    private final BankAccountDetailsService service;


    @GetMapping("/api/bank-account/details")
    public ResponseEntity<List<BankAccountWhitelist>> getBankAccountDetails() {
        return ResponseEntity.status(HttpStatus.OK).body(service.getBankAccountDetails());
    }

    @GetMapping("/api/transaction-detail-by-instruction")
    public ResponseEntity<Object> getTransactionDetailByInstructionId(@RequestParam String instructionId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getTransactionDetailByInstructionId(instructionId));
    }

}
