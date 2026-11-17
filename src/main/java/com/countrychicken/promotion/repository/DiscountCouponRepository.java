package com.countrychicken.promotion.repository;

import com.countrychicken.promotion.entity.DiscountCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DiscountCouponRepository extends JpaRepository<DiscountCoupon, UUID> {
    Optional<DiscountCoupon> findByCode(String code);
}
