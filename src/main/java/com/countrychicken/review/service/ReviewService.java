package com.countrychicken.review.service;

import com.countrychicken.common.exception.*;
import com.countrychicken.product.repository.ProductRepository;
import com.countrychicken.review.dto.*;
import com.countrychicken.review.entity.*;
import com.countrychicken.review.repository.ProductReviewRepository;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ProductReviewRepository reviews;
    private final ProductRepository products;
    private final UserRepository users;
    public ReviewService(ProductReviewRepository reviews, ProductRepository products, UserRepository users) {
        this.reviews = reviews; this.products = products; this.users = users;
    }
    @Transactional(readOnly = true)
    public List<ReviewResponse> list(UUID productId) {
        return reviews.findByProductIdAndStatusOrderByCreatedAtDesc(productId, ReviewStatus.APPROVED)
                .stream().map(ReviewResponse::from).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public double average(UUID productId) { return reviews.averageRating(productId, ReviewStatus.APPROVED); }
    @Transactional
    public ReviewResponse create(UUID userId, UUID productId, ReviewRequest request) {
        if (reviews.findByProductIdAndUserId(productId, userId).isPresent()) throw new ConflictException("You have already reviewed this product");
        var product = products.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        var user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ReviewResponse.from(reviews.save(new ProductReview(product, user, request.rating(), request.comment())));
    }
    @Transactional
    public ReviewResponse moderate(UUID reviewId, ReviewStatus status) {
        var review = reviews.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        review.moderate(status);
        return ReviewResponse.from(review);
    }
}
