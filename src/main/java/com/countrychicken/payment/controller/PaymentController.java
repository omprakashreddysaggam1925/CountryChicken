package com.countrychicken.payment.controller;

import com.countrychicken.commerce.dto.*;
import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.payment.service.PaymentService;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> create(Authentication auth, @Valid @RequestBody PaymentRequest body, HttpServletRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.create(id(auth), body), "Payment order created", r.getRequestURI()));
    }
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(Authentication auth, @PathVariable UUID paymentId,
                                                               @Valid @RequestBody RefundRequest body, HttpServletRequest r) {
        return ResponseEntity.ok(ApiResponse.success(service.refund(id(auth), paymentId, body.amount()), "Payment refunded", r.getRequestURI()));
    }
    private UUID id(Authentication auth) { return UUID.fromString(auth.getName()); }
}
