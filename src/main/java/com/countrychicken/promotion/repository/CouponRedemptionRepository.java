package com.countrychicken.promotion.repository;

import com.countrychicken.promotion.entity.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, UUID> {
    long countByCouponId(UUID couponId);
    long countByCouponIdAndUserId(UUID couponId, UUID userId);
}
