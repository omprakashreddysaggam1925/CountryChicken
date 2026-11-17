package com.countrychicken.loyalty.repository;

import com.countrychicken.loyalty.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ReferralRepository extends JpaRepository<Referral, UUID> {
    Optional<Referral> findByReferredId(UUID userId);
}
