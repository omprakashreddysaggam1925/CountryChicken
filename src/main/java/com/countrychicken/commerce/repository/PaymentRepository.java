package com.countrychicken.commerce.repository;
import com.countrychicken.commerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByIdempotencyKey(String key);
    Optional<Payment> findByProviderOrderId(String id);
    Optional<Payment> findByProviderPaymentId(String id);
    Optional<Payment> findByOrderId(UUID orderId);
    Optional<Payment> findByIdAndOrderUserId(UUID id, UUID userId);
}
