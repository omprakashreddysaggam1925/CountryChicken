package com.countrychicken.commerce.controller;

import com.countrychicken.commerce.dto.*;
import com.countrychicken.commerce.service.OrderService;
import com.countrychicken.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(Authentication auth, @Valid @RequestBody CheckoutRequest body, HttpServletRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.checkout(id(auth), body), "Order created", r.getRequestURI()));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> list(Authentication auth, HttpServletRequest r) {
        return ResponseEntity.ok(ApiResponse.success(service.list(id(auth)), "Orders retrieved", r.getRequestURI()));
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> get(Authentication auth, @PathVariable UUID orderId, HttpServletRequest r) {
        return ResponseEntity.ok(ApiResponse.success(service.get(id(auth), orderId), "Order retrieved", r.getRequestURI()));
    }
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(Authentication auth, @PathVariable UUID orderId, HttpServletRequest r) {
        return ResponseEntity.ok(ApiResponse.success(service.cancel(id(auth), orderId), "Order cancelled", r.getRequestURI()));
    }
    private UUID id(Authentication auth) { return UUID.fromString(auth.getName()); }
}
