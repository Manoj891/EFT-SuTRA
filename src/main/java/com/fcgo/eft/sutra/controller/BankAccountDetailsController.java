package com.fcgo.eft.sutra.controller;

import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import com.fcgo.eft.sutra.service.nonrealtime.NonRealTimeStatusFromNchl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BankAccountDetailsController {
    private final EftPaymentReceiveService paymentReceiveService;
    private final NonRealTimeStatusFromNchl statusFromNchl;


    @GetMapping("/api/nonrealtime/{batchId}")
    public ResponseEntity<Object> paymentStart(@PathVariable String batchId) {
        return ResponseEntity.status(HttpStatus.OK).body(statusFromNchl.checkByBatchId(batchId));
    }

}
