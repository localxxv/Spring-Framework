package org.example.carrent.controllers;

import org.example.carrent.dto.PaymentResponse;
import org.example.carrent.services.PaymentService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping({"/payments", "/api/payments"})
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/active")
    public PaymentResponse calculate(Authentication authentication) {
        return paymentService.calculateActivePayment(authentication.getName());
    }

    @PostMapping("/active/pay")
    public PaymentResponse pay(Authentication authentication) {
        return paymentService.payActiveRental(authentication.getName());
    }
}