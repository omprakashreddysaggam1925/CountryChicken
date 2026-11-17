package com.countrychicken.inventory.dto;

import com.countrychicken.inventory.entity.InventoryItem;

import java.util.UUID;

public record InventoryResponse(UUID id, UUID variantId, String sku, int onHand, int reserved,
                                int available, int lowStockThreshold) {
    public static InventoryResponse from(InventoryItem item) {
        return new InventoryResponse(item.getId(), item.getVariant().getId(), item.getVariant().getSku(),
                item.getOnHand(), item.getReserved(), item.getAvailable(), item.getLowStockThreshold());
    }
}
