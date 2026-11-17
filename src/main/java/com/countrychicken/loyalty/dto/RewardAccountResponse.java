package com.countrychicken.loyalty.dto;

import com.countrychicken.loyalty.entity.RewardAccount;
import java.util.UUID;

public record RewardAccountResponse(UUID userId, long balancePoints) {
    public static RewardAccountResponse from(RewardAccount account) {
        return new RewardAccountResponse(account.getUser().getId(), account.getBalancePoints());
    }
}
