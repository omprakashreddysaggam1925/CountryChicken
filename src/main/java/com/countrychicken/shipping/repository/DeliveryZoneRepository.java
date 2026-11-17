package com.countrychicken.shipping.repository;

import com.countrychicken.shipping.entity.DeliveryZone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, UUID> {
    Optional<DeliveryZone> findByCode(String code);
    List<DeliveryZone> findByActiveTrueOrderByPostalCodePrefixDesc();
}
