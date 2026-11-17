package com.countrychicken.review.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.review.dto.*;
import com.countrychicken.review.entity.ReviewStatus;
import com.countrychicken.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {
    private final ReviewService service;
    public ReviewController(ReviewService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> list(@PathVariable UUID productId, HttpServletRequest r) {
        return ResponseEntity.ok(ApiResponse.success(service.list(productId), "Reviews retrieved", r.getRequestURI()));
    }
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Double>> average(@PathVariable UUID productId, HttpServletRequest r) {
        return ResponseEntity.ok(ApiResponse.success(service.average(productId), "Review rating retrieved", r.getRequestURI()));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> create(Authentication auth, @PathVariable UUID productId,
                                                              @Valid @RequestBody ReviewRequest body, HttpServletRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                service.create(UUID.fromString(auth.getName()), productId, body), "Review submitted", r.getRequestURI()));
    }
}
