package com.countrychicken.security.dto;

import java.time.Instant;
import java.util.UUID;

public record TokenPairResponse(
        UUID userId,
        String accessToken,
        String refreshToken,
        String tokenType,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {
}
