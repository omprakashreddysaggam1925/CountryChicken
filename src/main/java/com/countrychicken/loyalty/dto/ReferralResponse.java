package com.countrychicken.loyalty.dto;

import com.countrychicken.loyalty.entity.*;
import java.util.UUID;

public record ReferralResponse(String referralCode, UUID referrerUserId, ReferralStatus status) {
    public static ReferralResponse from(ReferralCode code, Referral referral) {
        return new ReferralResponse(code.getCode(), referral == null ? null : referral.getReferrer().getId(),
                referral == null ? null : referral.getStatus());
    }
}
