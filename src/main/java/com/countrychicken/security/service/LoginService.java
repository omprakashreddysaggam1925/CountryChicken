package com.countrychicken.security.service;

import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.security.dto.LoginRequest;
import com.countrychicken.security.dto.LoginResponse;
import com.countrychicken.security.dto.TokenPairResponse;
import com.countrychicken.security.jwt.JwtTokenService;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.entity.UserStatus;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final Duration accessTokenTtl;
    private final RefreshTokenService refreshTokenService;

    public LoginService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            @Value("${security.jwt.access-token-ttl}") Duration accessTokenTtl,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())
                || user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Instant issuedAt = Instant.now();
        TokenPairResponse tokenPair = refreshTokenService.issue(user.getId(), user.getEmail());
        return new LoginResponse(
                tokenPair.userId(),
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                tokenPair.tokenType(),
                tokenPair.accessTokenExpiresAt()
        );
    }
}
