package com.countrychicken.commerce.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.product.entity.ProductVariant;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(name = "uk_cart_items_cart_variant", columnNames = {"cart_id", "variant_id"}))
public class CartItem extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "cart_id", nullable = false) private ShoppingCart cart;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "variant_id", nullable = false) private ProductVariant variant;
    @Column(nullable = false) private int quantity;
    @Column(name = "price_snapshot", nullable = false, precision = 12, scale = 2) private BigDecimal priceSnapshot;
    protected CartItem() {}
    public CartItem(ShoppingCart cart, ProductVariant variant, int quantity) {
        this.cart = cart; this.variant = variant; this.quantity = quantity; this.priceSnapshot = variant.getPrice();
    }
    public void changeQuantity(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); this.quantity = quantity; this.priceSnapshot = variant.getPrice(); }
    public ShoppingCart getCart() { return cart; } public ProductVariant getVariant() { return variant; }
    public int getQuantity() { return quantity; } public BigDecimal getPriceSnapshot() { return priceSnapshot; }
}
