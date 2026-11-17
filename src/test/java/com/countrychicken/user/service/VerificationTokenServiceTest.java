package com.countrychicken.user.service;

import com.countrychicken.user.entity.User;
import com.countrychicken.user.entity.UserVerification;
import com.countrychicken.user.entity.VerificationType;
import com.countrychicken.user.repository.UserRepository;
import com.countrychicken.user.repository.UserVerificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VerificationTokenServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserVerificationRepository verificationRepository = mock(UserVerificationRepository.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final VerificationTokenService service = new VerificationTokenService(
            userRepository,
            verificationRepository,
            passwordEncoder,
            new java.security.SecureRandom(),
            Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void createsHashedExpiringEmailToken() {
        User user = new User("customer@example.com", null, "hash", "Customer", null);
        when(verificationRepository.save(any(UserVerification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String rawToken = service.createEmailVerificationToken(user);

        assertThat(rawToken).hasSize(64);
        verify(verificationRepository).save(any(UserVerification.class));
    }

    @Test
    void verifiesEmailAndActivatesUser() {
        UUID userId = UUID.randomUUID();
        String rawToken = "verification-token";
        UserVerification verification = new UserVerification(
                userId,
                VerificationType.EMAIL,
                passwordEncoder.encode(rawToken),
                Instant.parse("2026-01-02T00:00:00Z")
        );
        User user = new User("customer@example.com", null, "hash", "Customer", null);
        when(verificationRepository
                .findTopByUserIdAndVerificationTypeAndVerifiedAtIsNullOrderByCreatedAtDesc(
                        userId,
                        VerificationType.EMAIL
                ))
                .thenReturn(Optional.of(verification));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        service.verifyEmail(userId, rawToken);

        assertThat(user.isEmailVerified()).isTrue();
        assertThat(user.getStatus().name()).isEqualTo("ACTIVE");
        assertThat(verification.getVerifiedAt()).isNotNull();
        verify(userRepository).save(user);
        verify(verificationRepository).save(verification);
    }
}
