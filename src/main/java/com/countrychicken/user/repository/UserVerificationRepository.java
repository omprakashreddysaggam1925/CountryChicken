package com.countrychicken.user.repository;

import com.countrychicken.user.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {

    Optional<UserVerification> findTopByUserIdAndVerificationTypeAndVerifiedAtIsNullOrderByCreatedAtDesc(
            UUID userId,
            com.countrychicken.user.entity.VerificationType verificationType
    );
}
