package com.countrychicken.promotion.service;

import com.countrychicken.commerce.entity.CustomerOrder;
import com.countrychicken.common.exception.*;
import com.countrychicken.promotion.dto.*;
import com.countrychicken.promotion.entity.*;
import com.countrychicken.promotion.repository.*;
import com.countrychicken.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class PromotionService {
    private final DiscountCouponRepository coupons;
    private final CouponRedemptionRepository redemptions;

    public PromotionService(DiscountCouponRepository coupons, CouponRedemptionRepository redemptions) {
        this.coupons = coupons; this.redemptions = redemptions;
    }

    @Transactional(readOnly = true)
    public BigDecimal discount(UUID userId, String code, BigDecimal subtotal) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;
        DiscountCoupon coupon = coupon(code);
        validate(coupon, userId, subtotal);
        return coupon.calculateDiscount(subtotal);
    }

    @Transactional
    public void redeem(UUID userId, String code, CustomerOrder order, BigDecimal amount) {
        if (code == null || code.isBlank() || amount.signum() == 0) return;
        DiscountCoupon coupon = coupon(code);
        validate(coupon, userId, order.getSubtotal());
        redemptions.save(new CouponRedemption(coupon, order.getUser(), order, amount));
    }

    @Transactional
    public CouponResponse create(CouponRequest request) {
        String code = normalize(request.code());
        if (coupons.findByCode(code).isPresent()) throw new ConflictException("Coupon code is already in use");
        if (!request.endsAt().isAfter(request.startsAt())) throw new ConflictException("Coupon end must be after its start");
        if (request.discountType() == DiscountType.PERCENTAGE && request.discountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ConflictException("Percentage discount cannot exceed 100");
        }
        return CouponResponse.from(coupons.save(new DiscountCoupon(code, request.discountType(), request.discountValue(),
                request.minimumSubtotal(), request.maximumDiscount(), request.startsAt(), request.endsAt(),
                request.usageLimit(), request.perCustomerLimit(), request.active())));
    }

    @Transactional(readOnly = true)
    public List<CouponResponse> list() { return coupons.findAll().stream().map(CouponResponse::from).toList(); }

    private DiscountCoupon coupon(String code) {
        return coupons.findByCode(normalize(code)).orElseThrow(() -> new ConflictException("Coupon is invalid"));
    }
    private void validate(DiscountCoupon coupon, UUID userId, BigDecimal subtotal) {
        if (!coupon.usableAt(Instant.now())) throw new ConflictException("Coupon is not active");
        if (coupon.calculateDiscount(subtotal).signum() == 0) throw new ConflictException("Coupon minimum subtotal was not met");
        if (coupon.getUsageLimit() != null && redemptions.countByCouponId(coupon.getId()) >= coupon.getUsageLimit()) {
            throw new ConflictException("Coupon usage limit was reached");
        }
        if (redemptions.countByCouponIdAndUserId(coupon.getId(), userId) >= coupon.getPerCustomerLimit()) {
            throw new ConflictException("Coupon customer usage limit was reached");
        }
    }
    private String normalize(String code) { return code.trim().toUpperCase(Locale.ROOT); }
}
