package com.countrychicken.product.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
public class ProductVariant extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "weight_grams", nullable = false)
    private int weightGrams;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active;

    protected ProductVariant() {
    }

    public ProductVariant(String sku, int weightGrams, BigDecimal price) {
        this.sku = sku;
        this.weightGrams = weightGrams;
        this.price = price;
        this.active = true;
    }

    void assignProduct(Product product) { this.product = product; }
    public Product getProduct() { return product; }
    public String getSku() { return sku; }
    public int getWeightGrams() { return weightGrams; }
    public BigDecimal getPrice() { return price; }
    public boolean isActive() { return active; }

    public void update(String sku, int weightGrams, BigDecimal price, boolean active) {
        this.sku = sku;
        this.weightGrams = weightGrams;
        this.price = price;
        this.active = active;
    }
}
