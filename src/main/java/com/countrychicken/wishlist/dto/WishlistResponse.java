package com.countrychicken.wishlist.dto;

import com.countrychicken.wishlist.entity.WishlistItem;
import java.time.Instant;
import java.util.UUID;

public record WishlistResponse(UUID id, UUID productId, String productName, String slug, Instant createdAt) {
    public static WishlistResponse from(WishlistItem item) {
        return new WishlistResponse(item.getId(), item.getProduct().getId(), item.getProduct().getName(), item.getProduct().getSlug(), item.getCreatedAt());
    }
}
