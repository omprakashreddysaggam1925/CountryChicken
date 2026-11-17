package com.countrychicken.user.service;

import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.entity.UserVerification;
import com.countrychicken.user.entity.UserStatus;
import com.countrychicken.user.entity.VerificationType;
import com.countrychicken.user.repository.UserRepository;
import com.countrychicken.user.repository.UserVerificationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(24);
    private final UserRepository userRepository;
    private final UserVerificationRepository verificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    private final Clock clock;

    @Autowired
    public VerificationTokenService(
            UserRepository userRepository,
            UserVerificationRepository verificationRepository,
            PasswordEncoder passwordEncoder
    ) {
        this(userRepository, verificationRepository, passwordEncoder, new SecureRandom(), Clock.systemUTC());
    }

    VerificationTokenService(
            UserRepository userRepository,
            UserVerificationRepository verificationRepository,
            PasswordEncoder passwordEncoder,
            SecureRandom secureRandom,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.secureRandom = secureRandom;
        this.clock = clock;
    }

    @Transactional
    public String createEmailVerificationToken(User user) {
        String rawToken = generateToken();
        UserVerification verification = new UserVerification(
                user.getId(),
                VerificationType.EMAIL,
                passwordEncoder.encode(rawToken),
                Instant.now(clock).plus(TOKEN_LIFETIME)
        );
        verificationRepository.save(verification);
        return rawToken;
    }

    @Transactional
    public void verifyEmail(UUID userId, String rawToken) {
        UserVerification verification = verificationRepository
                .findTopByUserIdAndVerificationTypeAndVerifiedAtIsNullOrderByCreatedAtDesc(
                        userId,
                        VerificationType.EMAIL
                )
                .orElseThrow(() -> new ResourceNotFoundException("Verification token was not found"));

        Instant now = Instant.now(clock);
        if (!verification.isUsableAt(now)
                || !passwordEncoder.matches(rawToken, verification.getTokenHash())) {
            throw new ResourceNotFoundException("Verification token is invalid or expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found"));
        verification.markVerified(now);
        user.activateAfterEmailVerification();
        userRepository.save(user);
        verificationRepository.save(verification);
    }

    private String generateToken() {
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return HexFormat.of().formatHex(token);
    }
}
