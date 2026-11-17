package com.countrychicken.review.dto;

import com.countrychicken.review.entity.ProductReview;
import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(UUID id, UUID productId, UUID userId, int rating, String comment, String status, Instant createdAt) {
    public static ReviewResponse from(ProductReview r) {
        return new ReviewResponse(r.getId(), r.getProduct().getId(), r.getUser().getId(), r.getRating(), r.getComment(), r.getStatus().name(), r.getCreatedAt());
    }
}
