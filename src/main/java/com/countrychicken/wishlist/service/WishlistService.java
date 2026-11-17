package com.countrychicken.wishlist.service;

import com.countrychicken.common.exception.*;
import com.countrychicken.product.repository.ProductRepository;
import com.countrychicken.user.repository.UserRepository;
import com.countrychicken.wishlist.dto.WishlistResponse;
import com.countrychicken.wishlist.entity.WishlistItem;
import com.countrychicken.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WishlistService {
    private final WishlistRepository wishlists; private final UserRepository users; private final ProductRepository products;
    public WishlistService(WishlistRepository wishlists, UserRepository users, ProductRepository products) {
        this.wishlists = wishlists; this.users = users; this.products = products;
    }
    @Transactional(readOnly = true)
    public List<WishlistResponse> list(UUID userId) { return wishlists.findByUserIdOrderByCreatedAtDesc(userId).stream().map(WishlistResponse::from).collect(Collectors.toList()); }
    @Transactional
    public WishlistResponse add(UUID userId, UUID productId) {
        if (wishlists.findByUserIdAndProductId(userId, productId).isPresent()) throw new ConflictException("Product is already in your wishlist");
        var user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var product = products.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return WishlistResponse.from(wishlists.save(new WishlistItem(user, product)));
    }
    @Transactional
    public void remove(UUID userId, UUID productId) {
        var item = wishlists.findByUserIdAndProductId(userId, productId).orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        wishlists.delete(item);
    }
}
