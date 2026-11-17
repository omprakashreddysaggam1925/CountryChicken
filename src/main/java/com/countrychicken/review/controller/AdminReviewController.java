package com.countrychicken.review.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.review.dto.ReviewResponse;
import com.countrychicken.review.entity.ReviewStatus;
import com.countrychicken.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reviews")
public class AdminReviewController {
    private final ReviewService service;
    public AdminReviewController(ReviewService service) { this.service = service; }
    @PutMapping("/{reviewId}/status")
    @PreAuthorize("hasAuthority('REVIEW_MODERATE')")
    public ResponseEntity<ApiResponse<ReviewResponse>> moderate(@PathVariable UUID reviewId, @RequestParam ReviewStatus status, HttpServletRequest r) {
        return ResponseEntity.ok(ApiResponse.success(service.moderate(reviewId, status), "Review moderated", r.getRequestURI()));
    }
}
