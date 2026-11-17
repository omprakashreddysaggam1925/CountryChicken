package com.countrychicken.inventory.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.product.entity.ProductVariant;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory_items")
public class InventoryItem extends AbstractEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false, unique = true)
    private ProductVariant variant;

    @Column(name = "on_hand", nullable = false)
    private int onHand;

    @Column(nullable = false)
    private int reserved;

    @Column(name = "low_stock_threshold", nullable = false)
    private int lowStockThreshold;

    protected InventoryItem() {
    }

    public InventoryItem(ProductVariant variant, int lowStockThreshold) {
        this.variant = variant;
        this.lowStockThreshold = lowStockThreshold;
    }

    public ProductVariant getVariant() { return variant; }
    public int getOnHand() { return onHand; }
    public int getReserved() { return reserved; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public int getAvailable() { return onHand - reserved; }

    public void adjustOnHand(int delta) {
        int next = onHand + delta;
        if (next < reserved) {
            throw new IllegalArgumentException("On-hand stock cannot be below reserved stock");
        }
        onHand = next;
    }

    public void reserve(int quantity) {
        if (quantity > getAvailable()) {
            throw new IllegalArgumentException("Insufficient available stock");
        }
        reserved += quantity;
    }

    public void release(int quantity) {
        if (quantity > reserved) {
            throw new IllegalArgumentException("Reserved stock is insufficient");
        }
        reserved -= quantity;
    }

    public void commit(int quantity) {
        if (quantity > reserved) {
            throw new IllegalArgumentException("Reserved stock is insufficient");
        }
        reserved -= quantity;
        onHand -= quantity;
    }

    public void updateThreshold(int threshold) {
        if (threshold < 0) throw new IllegalArgumentException("Low-stock threshold cannot be negative");
        lowStockThreshold = threshold;
    }
}
