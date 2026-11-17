package com.countrychicken.inventory.dto;

import com.countrychicken.inventory.entity.InventoryMovementType;
import jakarta.validation.constraints.*;

public record InventoryAdjustmentRequest(
        @NotNull Integer quantityDelta,
        @NotNull InventoryMovementType movementType,
        @Size(max = 500) String reason,
        @Min(0) Integer lowStockThreshold
) {
    public InventoryAdjustmentRequest {
        if (quantityDelta == null || quantityDelta == 0) {
            throw new IllegalArgumentException("Quantity delta cannot be zero");
        }
        if (movementType == null || movementType == InventoryMovementType.RESERVATION
                || movementType == InventoryMovementType.RELEASE || movementType == InventoryMovementType.COMMIT) {
            throw new IllegalArgumentException("Movement type is not valid for an adjustment");
        }
    }
}
