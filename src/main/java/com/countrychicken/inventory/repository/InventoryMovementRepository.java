package com.countrychicken.inventory.repository;

import com.countrychicken.inventory.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    List<InventoryMovement> findByInventoryItemIdOrderByCreatedAtDesc(UUID inventoryItemId);
}
