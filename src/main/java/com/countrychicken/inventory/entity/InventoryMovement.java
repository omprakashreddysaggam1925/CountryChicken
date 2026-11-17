package com.countrychicken.inventory.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 32)
    private InventoryMovementType movementType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(length = 500)
    private String reason;

    protected InventoryMovement() {
    }

    public InventoryMovement(InventoryItem inventoryItem, InventoryMovementType movementType,
                             int quantity, UUID referenceId, String reason) {
        this.inventoryItem = inventoryItem;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.reason = reason;
    }

    public InventoryMovementType getMovementType() { return movementType; }
    public int getQuantity() { return quantity; }
    public UUID getReferenceId() { return referenceId; }
    public String getReason() { return reason; }
}
