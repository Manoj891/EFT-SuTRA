package com.fcgo.eft.sutra.controller;

import com.fcgo.eft.sutra.dto.req.EftPaymentReceive;
import com.fcgo.eft.sutra.dto.req.PaymentRequestNew;
import com.fcgo.eft.sutra.exception.CustomException;
import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import com.fcgo.eft.sutra.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/add-payment-request-detail")
public class PaymentReceiveController {
    private final EftPaymentReceiveService paymentReceiveService;
    private final LoginService service;

    @PostMapping
    public ResponseEntity<String> paymentReceive(@RequestBody EftPaymentReceive receive, HttpServletRequest request) {
        String remoteIp = request.getRemoteAddr();
        service.checkValidId(remoteIp);
        paymentReceiveService.paymentReceive(receive);
        return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\":\"Successfully Receive Payment\"}");
    }

    @PutMapping
    public ResponseEntity<String> paymentReceiveNew(@RequestBody PaymentRequestNew receive, HttpServletRequest request) {
        String remoteIp = request.getRemoteAddr();
        service.checkValidId(remoteIp);
        paymentReceiveService.paymentReceive(receive);
        return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\":\"Successfully Receive Payment\"}");
    }

}
