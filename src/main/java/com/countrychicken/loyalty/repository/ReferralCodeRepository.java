package com.countrychicken.loyalty.repository;

import com.countrychicken.loyalty.entity.ReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ReferralCodeRepository extends JpaRepository<ReferralCode, UUID> {
    Optional<ReferralCode> findByCode(String code);
    Optional<ReferralCode> findByUserId(UUID userId);
}
