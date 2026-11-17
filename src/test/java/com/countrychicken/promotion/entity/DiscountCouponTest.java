package com.countrychicken.promotion.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class DiscountCouponTest {
    @Test
    void percentageDiscountHonorsMinimumAndMaximum() {
        Instant start = Instant.now().minusSeconds(10);
        DiscountCoupon coupon = new DiscountCoupon("save10", DiscountType.PERCENTAGE,
                new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("15"),
                start, start.plusSeconds(600), null, 1, true);

        assertEquals(new BigDecimal("0.00"), coupon.calculateDiscount(new BigDecimal("50")));
        assertEquals(new BigDecimal("15.00"), coupon.calculateDiscount(new BigDecimal("250")));
        assertTrue(coupon.usableAt(Instant.now()));
    }

    @Test
    void fixedDiscountCannotExceedSubtotal() {
        Instant now = Instant.now();
        DiscountCoupon coupon = new DiscountCoupon("save50", DiscountType.FIXED,
                new BigDecimal("50"), BigDecimal.ZERO, null,
                now.minusSeconds(1), now.plusSeconds(600), null, 1, true);

        assertEquals(new BigDecimal("20.00"), coupon.calculateDiscount(new BigDecimal("20")));
    }
}
