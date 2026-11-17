package com.countrychicken.commerce.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false, unique = true) private User user;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true) private List<CartItem> items = new ArrayList<>();
    protected ShoppingCart() {}
    public ShoppingCart(User user) { this.user = user; }
    public CartItem findItem(UUID variantId) { return items.stream().filter(i -> Objects.equals(i.getVariant().getId(), variantId)).findFirst().orElse(null); }
    public CartItem addItem(com.countrychicken.product.entity.ProductVariant variant, int quantity) {
        CartItem item = findItem(variant.getId());
        if (item != null) { item.changeQuantity(item.getQuantity() + quantity); return item; }
        item = new CartItem(this, variant, quantity); items.add(item); return item;
    }
    public void removeItem(UUID variantId) { items.removeIf(i -> Objects.equals(i.getVariant().getId(), variantId)); }
    public void clear() { items.clear(); }
    public User getUser() { return user; } public List<CartItem> getItems() { return Collections.unmodifiableList(items); }
}
