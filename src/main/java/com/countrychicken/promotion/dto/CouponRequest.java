package com.countrychicken.promotion.dto;

import com.countrychicken.promotion.entity.DiscountType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public record CouponRequest(
        @NotBlank @Size(max = 64) String code,
        @NotNull DiscountType discountType,
        @NotNull @DecimalMin("0.01") BigDecimal discountValue,
        @NotNull @DecimalMin("0.00") BigDecimal minimumSubtotal,
        @DecimalMin("0.01") BigDecimal maximumDiscount,
        @NotNull Instant startsAt,
        @NotNull Instant endsAt,
        @Positive Integer usageLimit,
        @Positive int perCustomerLimit,
        boolean active) {}
