package com.countrychicken.promotion.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "discount_coupons")
public class DiscountCoupon extends AbstractEntity {
    @Column(nullable = false, length = 64, unique = true) private String code;
    @Enumerated(EnumType.STRING) @Column(name = "discount_type", nullable = false, length = 16) private DiscountType discountType;
    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2) private BigDecimal discountValue;
    @Column(name = "minimum_subtotal", nullable = false, precision = 12, scale = 2) private BigDecimal minimumSubtotal;
    @Column(name = "maximum_discount", precision = 12, scale = 2) private BigDecimal maximumDiscount;
    @Column(name = "starts_at", nullable = false) private Instant startsAt;
    @Column(name = "ends_at", nullable = false) private Instant endsAt;
    @Column(name = "usage_limit") private Integer usageLimit;
    @Column(name = "per_customer_limit", nullable = false) private int perCustomerLimit;
    @Column(nullable = false) private boolean active;

    protected DiscountCoupon() {}
    public DiscountCoupon(String code, DiscountType type, BigDecimal value, BigDecimal minimumSubtotal,
                           BigDecimal maximumDiscount, Instant startsAt, Instant endsAt, Integer usageLimit,
                           int perCustomerLimit, boolean active) {
        this.code = code.trim().toUpperCase();
        this.discountType = type;
        this.discountValue = value;
        this.minimumSubtotal = minimumSubtotal;
        this.maximumDiscount = maximumDiscount;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.usageLimit = usageLimit;
        this.perCustomerLimit = perCustomerLimit;
        this.active = active;
    }
    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        if (subtotal.compareTo(minimumSubtotal) < 0) return BigDecimal.ZERO.setScale(2);
        BigDecimal discount = discountType == DiscountType.PERCENTAGE
                ? subtotal.multiply(discountValue).divide(BigDecimal.valueOf(100))
                : discountValue;
        if (maximumDiscount != null) discount = discount.min(maximumDiscount);
        return discount.min(subtotal).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    public boolean usableAt(Instant now) { return active && !now.isBefore(startsAt) && now.isBefore(endsAt); }
    public String getCode() { return code; } public DiscountType getDiscountType() { return discountType; }
    public BigDecimal getDiscountValue() { return discountValue; } public BigDecimal getMinimumSubtotal() { return minimumSubtotal; }
    public BigDecimal getMaximumDiscount() { return maximumDiscount; } public Instant getStartsAt() { return startsAt; }
    public Instant getEndsAt() { return endsAt; } public Integer getUsageLimit() { return usageLimit; }
    public int getPerCustomerLimit() { return perCustomerLimit; } public boolean isActive() { return active; }
}
