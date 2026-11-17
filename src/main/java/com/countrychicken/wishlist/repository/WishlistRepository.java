package com.countrychicken.wishlist.repository;

import com.countrychicken.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface WishlistRepository extends JpaRepository<WishlistItem, UUID> {
    List<WishlistItem> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<WishlistItem> findByUserIdAndProductId(UUID userId, UUID productId);
}
