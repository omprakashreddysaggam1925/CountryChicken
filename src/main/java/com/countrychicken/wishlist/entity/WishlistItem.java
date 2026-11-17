package com.countrychicken.wishlist.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.product.entity.Product;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "wishlists", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class WishlistItem extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id", nullable = false) private Product product;
    protected WishlistItem() {}
    public WishlistItem(User user, Product product) { this.user = user; this.product = product; }
    public Product getProduct() { return product; }
}
