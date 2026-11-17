package com.countrychicken.promotion.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.promotion.dto.*;
import com.countrychicken.promotion.service.PromotionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin/promotions/coupons")
public class AdminPromotionController {
    private final PromotionService service;
    public AdminPromotionController(PromotionService service) { this.service = service; }
    @GetMapping
    @PreAuthorize("hasAuthority('PROMOTION_READ')")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> list(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.list(), "Coupons retrieved", request.getRequestURI()));
    }
    @PostMapping
    @PreAuthorize("hasAuthority('PROMOTION_CREATE')")
    public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody CouponRequest body, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.create(body), "Coupon created", request.getRequestURI()));
    }
}
