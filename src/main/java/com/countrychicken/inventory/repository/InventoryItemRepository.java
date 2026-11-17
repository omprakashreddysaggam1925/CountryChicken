package com.countrychicken.inventory.repository;

import com.countrychicken.inventory.entity.InventoryItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    Optional<InventoryItem> findByVariantId(UUID variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InventoryItem i where i.variant.id = :variantId")
    Optional<InventoryItem> findByVariantIdForUpdate(UUID variantId);
}
