package com.countrychicken.product.dto;

import com.countrychicken.product.entity.ProductVariant;

import java.math.BigDecimal;
import java.util.UUID;

public record VariantResponse(UUID id, String sku, int weightGrams, BigDecimal price, boolean active) {
    public static VariantResponse from(ProductVariant variant) {
        return new VariantResponse(variant.getId(), variant.getSku(), variant.getWeightGrams(),
                variant.getPrice(), variant.isActive());
    }
}
