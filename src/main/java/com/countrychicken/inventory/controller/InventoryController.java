package com.countrychicken.inventory.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.inventory.dto.*;
import com.countrychicken.inventory.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/admin/inventory")
public class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service) { this.service = service; }

    @GetMapping("/{variantId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    public ResponseEntity<ApiResponse<InventoryResponse>> get(@PathVariable UUID variantId, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.get(variantId), "Inventory retrieved", request.getRequestURI()));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> lowStock(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.lowStock(), "Low-stock inventory retrieved", request.getRequestURI()));
    }

    @GetMapping("/{variantId}/movements")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    public ResponseEntity<ApiResponse<List<MovementResponse>>> movements(@PathVariable UUID variantId, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.movements(variantId), "Inventory movements retrieved", request.getRequestURI()));
    }

    @PostMapping("/{variantId}/adjustments")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    public ResponseEntity<ApiResponse<InventoryResponse>> adjust(@PathVariable UUID variantId,
                                                                  @Valid @RequestBody InventoryAdjustmentRequest body,
                                                                  HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.adjust(variantId, body), "Inventory adjusted", request.getRequestURI()));
    }

    @PostMapping("/{variantId}/reservations")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    public ResponseEntity<ApiResponse<ReservationResponse>> reserve(@PathVariable UUID variantId,
                                                                     @Valid @RequestBody ReserveStockRequest body,
                                                                     HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.reserve(variantId, body), "Stock reserved", request.getRequestURI()));
    }

    @PostMapping("/reservations/{reservationId}/release")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    public ResponseEntity<ApiResponse<ReservationResponse>> release(@PathVariable UUID reservationId, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.release(reservationId), "Stock reservation released", request.getRequestURI()));
    }

    @PostMapping("/reservations/{reservationId}/commit")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    public ResponseEntity<ApiResponse<ReservationResponse>> commit(@PathVariable UUID reservationId, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.commit(reservationId), "Stock reservation committed", request.getRequestURI()));
    }
}
