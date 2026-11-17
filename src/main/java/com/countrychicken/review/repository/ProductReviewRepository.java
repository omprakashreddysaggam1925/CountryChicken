package com.countrychicken.review.repository;

import com.countrychicken.review.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {
    List<ProductReview> findByProductIdAndStatusOrderByCreatedAtDesc(UUID productId, ReviewStatus status);
    Optional<ProductReview> findByProductIdAndUserId(UUID productId, UUID userId);
    long countByProductIdAndStatus(UUID productId, ReviewStatus status);
    @Query("select coalesce(avg(r.rating), 0) from ProductReview r where r.product.id = :productId and r.status = :status")
    double averageRating(UUID productId, ReviewStatus status);
}
