package com.countrychicken.commerce.repository;
import com.countrychicken.commerce.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, UUID> {
    List<DeliveryAddress> findByUserIdOrderByDefaultAddressDescCreatedAtDesc(UUID userId);
    Optional<DeliveryAddress> findByIdAndUserId(UUID id, UUID userId);
    List<DeliveryAddress> findByUserId(UUID userId);
}
