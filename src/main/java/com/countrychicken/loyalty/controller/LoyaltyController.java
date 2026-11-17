package com.countrychicken.loyalty.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.loyalty.dto.*;
import com.countrychicken.loyalty.service.LoyaltyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account/loyalty")
public class LoyaltyController {
    private final LoyaltyService service;
    public LoyaltyController(LoyaltyService service) { this.service = service; }
    @GetMapping("/rewards")
    public ResponseEntity<ApiResponse<RewardAccountResponse>> rewards(Authentication auth, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.account(id(auth)), "Reward balance retrieved", request.getRequestURI()));
    }
    @GetMapping("/referral")
    public ResponseEntity<ApiResponse<ReferralResponse>> referral(Authentication auth, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.referral(id(auth)), "Referral details retrieved", request.getRequestURI()));
    }
    private UUID id(Authentication auth) { return UUID.fromString(auth.getName()); }
}
