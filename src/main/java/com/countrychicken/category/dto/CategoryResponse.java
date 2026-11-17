package com.countrychicken.category.dto;

import com.countrychicken.category.entity.Category;

import java.util.UUID;

public record CategoryResponse(UUID id, UUID parentId, String name, String slug,
                               String description, boolean active) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getParentId(), category.getName(),
                category.getSlug(), category.getDescription(), category.isActive());
    }
}
