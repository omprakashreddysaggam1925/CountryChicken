package com.countrychicken.commerce.service;

import com.countrychicken.commerce.dto.*;
import com.countrychicken.commerce.entity.*;
import com.countrychicken.commerce.repository.ShoppingCartRepository;
import com.countrychicken.common.exception.*;
import com.countrychicken.inventory.entity.InventoryItem;
import com.countrychicken.inventory.repository.InventoryItemRepository;
import com.countrychicken.product.entity.*;
import com.countrychicken.product.repository.ProductVariantRepository;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class CartService {
    private final ShoppingCartRepository carts;
    private final UserRepository users;
    private final ProductVariantRepository variants;
    private final InventoryItemRepository inventory;
    public CartService(ShoppingCartRepository carts, UserRepository users, ProductVariantRepository variants, InventoryItemRepository inventory) {
        this.carts = carts; this.users = users; this.variants = variants; this.inventory = inventory;
    }
    @Transactional
    public CartResponse get(UUID userId) { return CartResponse.from(cart(userId)); }
    @Transactional
    public CartResponse add(UUID userId, CartItemRequest request) {
        ShoppingCart cart = cart(userId);
        ProductVariant variant = activeVariant(request.variantId());
        int existing = Optional.ofNullable(cart.findItem(variant.getId())).map(CartItem::getQuantity).orElse(0);
        ensureStock(variant.getId(), existing + request.quantity());
        cart.addItem(variant, request.quantity());
        return CartResponse.from(cart);
    }
    @Transactional
    public CartResponse update(UUID userId, UUID variantId, CartItemRequest request) {
        if (!variantId.equals(request.variantId())) throw new ConflictException("Variant IDs do not match");
        ShoppingCart cart = cart(userId);
        CartItem item = Optional.ofNullable(cart.findItem(variantId)).orElseThrow(() -> new ResourceNotFoundException("Cart item was not found"));
        ProductVariant variant = activeVariant(variantId);
        ensureStock(variantId, request.quantity());
        item.changeQuantity(request.quantity());
        return CartResponse.from(cart);
    }
    @Transactional
    public CartResponse remove(UUID userId, UUID variantId) { ShoppingCart cart = cart(userId); if (cart.findItem(variantId) == null) throw new ResourceNotFoundException("Cart item was not found"); cart.removeItem(variantId); return CartResponse.from(cart); }
    private ShoppingCart cart(UUID userId) { return carts.findByUserId(userId).orElseGet(() -> carts.save(new ShoppingCart(users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User was not found"))))); }
    private ProductVariant activeVariant(UUID id) {
        ProductVariant v = variants.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product variant was not found"));
        if (!v.isActive() || v.getProduct().getStatus() != ProductStatus.ACTIVE || !v.getProduct().getCategory().isActive()) throw new ConflictException("Product variant is not available");
        return v;
    }
    private void ensureStock(UUID variantId, int quantity) {
        InventoryItem item = inventory.findByVariantId(variantId).orElseThrow(() -> new ConflictException("No inventory is configured for this variant"));
        if (item.getAvailable() < quantity) throw new ConflictException("Requested quantity exceeds available stock");
    }
}
