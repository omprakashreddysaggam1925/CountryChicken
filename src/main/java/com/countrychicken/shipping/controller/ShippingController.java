package com.countrychicken.shipping.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.shipping.dto.*;
import com.countrychicken.shipping.service.ShippingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipping")
public class ShippingController {
    private final ShippingService service;
    public ShippingController(ShippingService service) { this.service = service; }

    @GetMapping("/quote")
    public ResponseEntity<ApiResponse<DeliveryZoneResponse>> quote(@RequestParam String postalCode, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.quote(postalCode), "Shipping quote retrieved", request.getRequestURI()));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> get(Authentication auth, @PathVariable UUID orderId, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.getForCustomer(id(auth), orderId), "Shipment retrieved", request.getRequestURI()));
    }

    private UUID id(Authentication auth) { return UUID.fromString(auth.getName()); }
}
