package com.countrychicken.user.service;

import com.countrychicken.common.exception.ConflictException;
import com.countrychicken.user.dto.RegisterUserRequest;
import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import com.countrychicken.loyalty.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final LoyaltyService loyaltyService;

    @Autowired
    public UserRegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            VerificationTokenService verificationTokenService,
            LoyaltyService loyaltyService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.loyaltyService = loyaltyService;
    }

    public UserRegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            VerificationTokenService verificationTokenService
    ) {
        this(userRepository, passwordEncoder, verificationTokenService, null);
    }

    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account already exists for this email");
        }
        if (request.mobileNumber() != null
                && !request.mobileNumber().isBlank()
                && userRepository.existsByMobileNumber(request.mobileNumber().trim())) {
            throw new ConflictException("An account already exists for this mobile number");
        }

        User user = new User(
                email,
                normalizeOptional(request.mobileNumber()),
                passwordEncoder.encode(request.password()),
                request.firstName().trim(),
                normalizeOptional(request.lastName())
        );
        User savedUser = userRepository.save(user);
        if (loyaltyService != null) {
            loyaltyService.initialize(savedUser, request.referralCode());
        }
        verificationTokenService.createEmailVerificationToken(savedUser);
        return UserResponse.from(savedUser);
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
