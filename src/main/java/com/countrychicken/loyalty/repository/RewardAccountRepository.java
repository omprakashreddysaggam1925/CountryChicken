package com.countrychicken.loyalty.repository;

import com.countrychicken.loyalty.entity.RewardAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface RewardAccountRepository extends JpaRepository<RewardAccount, UUID> {
    Optional<RewardAccount> findByUserId(UUID userId);
}
