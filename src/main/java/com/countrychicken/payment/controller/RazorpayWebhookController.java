package com.countrychicken.payment.controller;

import com.countrychicken.payment.gateway.RazorpayGateway;
import com.countrychicken.payment.service.PaymentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/webhooks/razorpay")
public class RazorpayWebhookController {
    private final RazorpayGateway gateway;
    private final PaymentService service;
    public RazorpayWebhookController(RazorpayGateway gateway, PaymentService service) { this.gateway = gateway; this.service = service; }
    @PostMapping
    public ResponseEntity<Void> receive(@RequestHeader("X-Razorpay-Signature") String signature, @RequestBody String payload) {
        if (!gateway.verifyWebhook(payload, signature)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        service.webhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
