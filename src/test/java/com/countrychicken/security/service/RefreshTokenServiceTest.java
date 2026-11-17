package com.countrychicken.security.service;

import com.countrychicken.security.entity.RefreshToken;
import com.countrychicken.security.jwt.JwtTokenService;
import com.countrychicken.security.repository.RefreshTokenRepository;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenServiceTest {

    private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    private final RefreshTokenService service = new RefreshTokenService(
            Duration.ofMinutes(15),
            Duration.ofDays(30),
            refreshTokenRepository,
            userRepository,
            jwtTokenService,
            new java.security.SecureRandom(),
            clock
    );

    @Test
    void rotatesTokenAndRevokesPreviousToken() {
        UUID userId = UUID.randomUUID();
        User user = new User("customer@example.com", null, "hash", "Customer", null);
        RefreshToken stored = new RefreshToken(
                userId,
                hash("refresh-token"),
                Instant.parse("2026-02-01T00:00:00Z")
        );
        when(refreshTokenRepository.findByTokenHash(hash("refresh-token"))).thenReturn(Optional.of(stored));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtTokenService.issueAccessToken(any(), any())).thenReturn("access-token");

        var response = service.rotate("refresh-token");

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(stored.getRevokedAt()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
        verify(refreshTokenRepository).save(stored);
    }

    @Test
    void logoutRevokesMatchingToken() {
        RefreshToken stored = new RefreshToken(
                UUID.randomUUID(),
                hash("refresh-token"),
                Instant.parse("2026-02-01T00:00:00Z")
        );
        when(refreshTokenRepository.findByTokenHash(hash("refresh-token"))).thenReturn(Optional.of(stored));

        service.revoke("refresh-token");

        assertThat(stored.getRevokedAt()).isNotNull();
        verify(refreshTokenRepository).save(stored);
    }

    private static String hash(String token) {
        try {
            return java.util.HexFormat.of().formatHex(
                    java.security.MessageDigest.getInstance("SHA-256")
                            .digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8))
            );
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
