package com.countrychicken.promotion.entity;

import com.countrychicken.commerce.entity.CustomerOrder;
import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "coupon_redemptions")
public class CouponRedemption extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "coupon_id", nullable = false) private DiscountCoupon coupon;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false, unique = true) private CustomerOrder order;
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2) private BigDecimal discountAmount;
    protected CouponRedemption() {}
    public CouponRedemption(DiscountCoupon coupon, User user, CustomerOrder order, BigDecimal discountAmount) {
        this.coupon = coupon; this.user = user; this.order = order; this.discountAmount = discountAmount;
    }
}
