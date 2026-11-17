package com.countrychicken.loyalty.repository;

import com.countrychicken.loyalty.entity.RewardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface RewardTransactionRepository extends JpaRepository<RewardTransaction, UUID> {
    boolean existsByReferenceKey(String referenceKey);
    Optional<RewardTransaction> findByReferenceKey(String referenceKey);
}
