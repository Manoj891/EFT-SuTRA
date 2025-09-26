package com.fcgo.eft.sutra.controller;

import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/add-payment-request-detail")
public class PaymentReceiveController {
    private final EftPaymentReceiveService paymentReceiveService;

    @PostMapping
    public ResponseEntity<String> paymentReceive(@RequestBody EftPaymentReceive receive) {
        paymentReceiveService.paymentReceive(receive);
        return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\":\"Successfully Receive Payment\"}");
    }

}
