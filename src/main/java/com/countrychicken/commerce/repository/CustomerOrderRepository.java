package com.countrychicken.commerce.repository;
import com.countrychicken.commerce.entity.CustomerOrder;
import com.countrychicken.commerce.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {
    List<CustomerOrder> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<CustomerOrder> findByIdAndUserId(UUID id, UUID userId);
    long countByStatus(OrderStatus status);
}
