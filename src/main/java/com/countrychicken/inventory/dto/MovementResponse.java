package com.countrychicken.inventory.dto;

import com.countrychicken.inventory.entity.InventoryMovement;

import java.time.Instant;
import java.util.UUID;

public record MovementResponse(UUID id, String type, int quantity, UUID referenceId,
                               String reason, Instant createdAt) {
    public static MovementResponse from(InventoryMovement movement) {
        return new MovementResponse(movement.getId(), movement.getMovementType().name(), movement.getQuantity(),
                movement.getReferenceId(), movement.getReason(), movement.getCreatedAt());
    }
}
