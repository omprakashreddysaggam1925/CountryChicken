package com.countrychicken.promotion.dto;

import com.countrychicken.promotion.entity.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CouponResponse(UUID id, String code, DiscountType discountType, BigDecimal discountValue,
                             BigDecimal minimumSubtotal, BigDecimal maximumDiscount, Instant startsAt,
                             Instant endsAt, Integer usageLimit, int perCustomerLimit, boolean active) {
    public static CouponResponse from(DiscountCoupon c) {
        return new CouponResponse(c.getId(), c.getCode(), c.getDiscountType(), c.getDiscountValue(),
                c.getMinimumSubtotal(), c.getMaximumDiscount(), c.getStartsAt(), c.getEndsAt(),
                c.getUsageLimit(), c.getPerCustomerLimit(), c.isActive());
    }
}
