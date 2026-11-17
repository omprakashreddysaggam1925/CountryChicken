package com.countrychicken.shipping.repository;

import com.countrychicken.shipping.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    Optional<Shipment> findByOrderId(UUID orderId);
    Optional<Shipment> findByIdAndOrderUserId(UUID id, UUID userId);
}
