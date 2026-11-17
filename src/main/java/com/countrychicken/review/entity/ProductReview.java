package com.countrychicken.review.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.product.entity.Product;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "product_reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "user_id"}))
public class ProductReview extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false) private int rating;
    @Column(columnDefinition = "TEXT") private String comment;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private ReviewStatus status;
    protected ProductReview() {}
    public ProductReview(Product product, User user, int rating, String comment) {
        this.product = product; this.user = user; this.rating = rating; this.comment = comment; this.status = ReviewStatus.PENDING;
    }
    public Product getProduct() { return product; } public User getUser() { return user; } public int getRating() { return rating; }
    public String getComment() { return comment; } public ReviewStatus getStatus() { return status; }
    public void moderate(ReviewStatus status) { this.status = status; }
}
