package com.motherandbabymilk.controller;

import com.motherandbabymilk.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@SecurityRequirement(name = "api")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePayment(
            @RequestParam int orderId,
            HttpServletRequest request) {

        String paymentUrl = paymentService.generatePayment(orderId, request);
        if (paymentUrl != null) {
            return ResponseEntity.ok(paymentUrl);
        } else {
            return ResponseEntity.badRequest().body("Failed to generate VNPay URL");
        }
    }

    @GetMapping("/callback")
    public String vnpayCallback(@RequestParam(required = false) String url) {
        return paymentService.handleVnpayCallback(url);
    }

    @PostMapping("/preorder/generate")
    public ResponseEntity<String> generatePreOrderPayment(
            @RequestParam int preOrderId,
            HttpServletRequest request) {

        String paymentUrl = paymentService.generatePreOrderPayment(preOrderId, request);
        if (paymentUrl != null) {
            return ResponseEntity.ok(paymentUrl);
        } else {
            return ResponseEntity.badRequest().body("Failed to generate VNPay URL for PreOrder");
        }
    }

    @GetMapping("/preorder/callback")
    public ResponseEntity<String> handlePreOrderVnpayCallback(@RequestParam(required = false) String url) {
        String result = paymentService.handlePreOrderVnpayCallback(url);
        return ResponseEntity.ok(result);
    }
}

