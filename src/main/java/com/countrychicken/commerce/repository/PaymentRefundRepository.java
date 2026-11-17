package com.countrychicken.commerce.repository;
import com.countrychicken.commerce.entity.PaymentRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, UUID> {}
