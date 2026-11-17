package com.countrychicken.product.dto;

import com.countrychicken.product.entity.Product;
import com.countrychicken.product.entity.ProductStatus;

import java.util.List;
import java.util.UUID;

public record ProductResponse(UUID id, UUID categoryId, String name, String slug, String description,
                              ProductStatus status, List<VariantResponse> variants) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getCategory().getId(), product.getName(),
                product.getSlug(), product.getDescription(), product.getStatus(),
                product.getVariants().stream().map(VariantResponse::from).toList());
    }
}
