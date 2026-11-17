package com.countrychicken.commerce.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.inventory.entity.StockReservation;
import com.countrychicken.product.entity.ProductVariant;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "customer_order_items")
public class OrderItem extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private CustomerOrder order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "variant_id", nullable = false) private ProductVariant variant;
    @Column(name = "sku_snapshot", nullable = false, length = 100) private String skuSnapshot;
    @Column(name = "product_name_snapshot", nullable = false, length = 200) private String productNameSnapshot;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2) private BigDecimal unitPrice;
    @Column(nullable = false) private int quantity;
    @Column(name = "line_total", nullable = false, precision = 12, scale = 2) private BigDecimal lineTotal;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "stock_reservation_id", nullable = false) private StockReservation stockReservation;
    protected OrderItem() {}
    public OrderItem(CustomerOrder order, ProductVariant variant, int quantity, StockReservation reservation) {
        this.order = order; this.variant = variant; this.skuSnapshot = variant.getSku(); this.productNameSnapshot = variant.getProduct().getName();
        this.unitPrice = variant.getPrice(); this.quantity = quantity; this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity)); this.stockReservation = reservation;
    }
    public ProductVariant getVariant() { return variant; } public String getSkuSnapshot() { return skuSnapshot; }
    public String getProductNameSnapshot() { return productNameSnapshot; } public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; } public BigDecimal getLineTotal() { return lineTotal; }
    public StockReservation getStockReservation() { return stockReservation; }
}
