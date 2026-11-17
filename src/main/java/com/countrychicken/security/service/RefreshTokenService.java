package com.countrychicken.security.service;

import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.security.dto.TokenPairResponse;
import com.countrychicken.security.entity.RefreshToken;
import com.countrychicken.security.repository.RefreshTokenRepository;
import com.countrychicken.security.jwt.JwtTokenService;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final SecureRandom secureRandom;
    private final Clock clock;

    @Autowired
    public RefreshTokenService(
            @Value("${security.jwt.access-token-ttl}") Duration accessTokenTtl,
            @Value("${security.jwt.refresh-token-ttl}") Duration refreshTokenTtl,
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            JwtTokenService jwtTokenService
    ) {
        this(
                accessTokenTtl,
                refreshTokenTtl,
                refreshTokenRepository,
                userRepository,
                jwtTokenService,
                new SecureRandom(),
                Clock.systemUTC()
        );
    }

    RefreshTokenService(
            Duration accessTokenTtl,
            Duration refreshTokenTtl,
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            JwtTokenService jwtTokenService,
            SecureRandom secureRandom,
            Clock clock
    ) {
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenTtl = refreshTokenTtl;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
        this.secureRandom = secureRandom;
        this.clock = clock;
    }

    @Transactional
    public TokenPairResponse issue(UUID userId, String email) {
        Instant now = Instant.now(clock);
        String rawRefreshToken = generateToken();
        refreshTokenRepository.save(new RefreshToken(
                userId,
                hashToken(rawRefreshToken),
                now.plus(refreshTokenTtl)
        ));
        return new TokenPairResponse(
                userId,
                jwtTokenService.issueAccessToken(userId, email),
                rawRefreshToken,
                "Bearer",
                now.plus(accessTokenTtl),
                now.plus(refreshTokenTtl)
        );
    }

    @Transactional
    public TokenPairResponse rotate(String rawRefreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(hashToken(rawRefreshToken))
                .filter(token -> token.isUsableAt(Instant.now(clock)))
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token is invalid or expired"));

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User was not found"));
        storedToken.revoke(Instant.now(clock));
        refreshTokenRepository.save(storedToken);
        return issue(user.getId(), user.getEmail());
    }

    @Transactional
    public void revoke(String rawRefreshToken) {
        refreshTokenRepository.findByTokenHash(hashToken(rawRefreshToken))
                .filter(token -> token.isUsableAt(Instant.now(clock)))
                .ifPresent(token -> {
                    token.revoke(Instant.now(clock));
                    refreshTokenRepository.save(token);
                });
    }

    private String generateToken() {
        byte[] token = new byte[48];
        secureRandom.nextBytes(token);
        return HexFormat.of().formatHex(token);
    }

    private String hashToken(String rawToken) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

}
