package com.countrychicken.inventory.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "stock_reservations")
public class StockReservation extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReservationStatus status;

    protected StockReservation() {
    }

    public StockReservation(InventoryItem inventoryItem, int quantity, Instant expiresAt) {
        this.inventoryItem = inventoryItem;
        this.quantity = quantity;
        this.expiresAt = expiresAt;
        this.status = ReservationStatus.ACTIVE;
    }

    public InventoryItem getInventoryItem() { return inventoryItem; }
    public int getQuantity() { return quantity; }
    public Instant getExpiresAt() { return expiresAt; }
    public ReservationStatus getStatus() { return status; }
    public void release() { status = ReservationStatus.RELEASED; }
    public void commit() { status = ReservationStatus.COMMITTED; }
    public void expire() { status = ReservationStatus.EXPIRED; }
}
