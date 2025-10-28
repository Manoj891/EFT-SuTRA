package com.fcgo.eft.sutra.controller;


import com.fcgo.eft.sutra.dto.req.LoginReq;
import com.fcgo.eft.sutra.dto.res.LoginRes;
import com.fcgo.eft.sutra.dto.res.PaymentReceiveStatus;
import com.fcgo.eft.sutra.service.EftPaymentReceiveService;
import com.fcgo.eft.sutra.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/api/")
public class LoginController {
    private final LoginService service;
    private final EftPaymentReceiveService paymentReceiveService;

    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq req, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.login(req, request));
    }

    @GetMapping("/payment-start")
    public ResponseEntity<Void> paymentSTart() {
        paymentReceiveService.startTransactionThread(PaymentReceiveStatus.builder().offus(1).onus(1).build());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
