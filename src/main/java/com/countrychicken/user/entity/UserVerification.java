package com.countrychicken.user.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_verifications")
public class UserVerification extends AbstractEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_type", nullable = false, length = 32)
    private VerificationType verificationType;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    protected UserVerification() {
    }

    public UserVerification(
            UUID userId,
            VerificationType verificationType,
            String tokenHash,
            Instant expiresAt
    ) {
        this.userId = userId;
        this.verificationType = verificationType;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public VerificationType getVerificationType() {
        return verificationType;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public boolean isUsableAt(Instant now) {
        return verifiedAt == null && expiresAt.isAfter(now);
    }

    public void markVerified(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
