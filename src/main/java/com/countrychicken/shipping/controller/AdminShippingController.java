package com.countrychicken.shipping.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.shipping.dto.*;
import com.countrychicken.shipping.service.ShippingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin/shipping")
public class AdminShippingController {
    private final ShippingService service;
    public AdminShippingController(ShippingService service) { this.service = service; }

    @GetMapping("/zones")
    @PreAuthorize("hasAuthority('SHIPPING_READ')")
    public ResponseEntity<ApiResponse<List<DeliveryZoneResponse>>> zones(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.listZones(), "Delivery zones retrieved", request.getRequestURI()));
    }

    @PostMapping("/zones")
    @PreAuthorize("hasAuthority('SHIPPING_CREATE')")
    public ResponseEntity<ApiResponse<DeliveryZoneResponse>> createZone(@Valid @RequestBody DeliveryZoneRequest body, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.createZone(body), "Delivery zone created", request.getRequestURI()));
    }

    @PutMapping("/zones/{zoneId}")
    @PreAuthorize("hasAuthority('SHIPPING_UPDATE')")
    public ResponseEntity<ApiResponse<DeliveryZoneResponse>> updateZone(@PathVariable UUID zoneId, @Valid @RequestBody DeliveryZoneRequest body, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.updateZone(zoneId, body), "Delivery zone updated", request.getRequestURI()));
    }

    @PostMapping("/shipments/{shipmentId}/status")
    @PreAuthorize("hasAuthority('SHIPPING_UPDATE')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateStatus(@PathVariable UUID shipmentId, @Valid @RequestBody ShipmentStatusRequest body, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.updateStatus(shipmentId, body.status()), "Shipment status updated", request.getRequestURI()));
    }
}
