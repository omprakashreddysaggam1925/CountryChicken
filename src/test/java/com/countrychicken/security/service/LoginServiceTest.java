package com.countrychicken.security.service;

import com.countrychicken.security.dto.LoginRequest;
import com.countrychicken.security.dto.LoginResponse;
import com.countrychicken.security.jwt.JwtTokenService;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
    private final RefreshTokenService refreshTokenService = mock(RefreshTokenService.class);
    private final LoginService service = new LoginService(
            userRepository,
            passwordEncoder,
            jwtTokenService,
            Duration.ofMinutes(15),
            refreshTokenService
    );

    @Test
    void rejectsUnverifiedUser() {
        User user = new User(
                "customer@example.com",
                null,
                passwordEncoder.encode("very-secure-password"),
                "Customer",
                null
        );
        when(userRepository.findByEmailIgnoreCase("customer@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.login(new LoginRequest(
                "customer@example.com",
                "very-secure-password"
        ))).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void returnsAccessTokenForActiveUser() {
        User user = new User(
                "customer@example.com",
                null,
                passwordEncoder.encode("very-secure-password"),
                "Customer",
                null
        );
        user.activateAfterEmailVerification();
        when(userRepository.findByEmailIgnoreCase("customer@example.com")).thenReturn(Optional.of(user));
        when(refreshTokenService.issue(any(), any())).thenReturn(new com.countrychicken.security.dto.TokenPairResponse(
                UUID.randomUUID(),
                "signed-token",
                "refresh-token",
                "Bearer",
                java.time.Instant.now().plusSeconds(900),
                java.time.Instant.now().plusSeconds(86400)
        ));

        LoginResponse response = service.login(new LoginRequest(
                "customer@example.com",
                "very-secure-password"
        ));

        assertThat(response.accessToken()).isEqualTo("signed-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }
}
