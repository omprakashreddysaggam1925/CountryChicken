package com.countrychicken.commerce.repository;
import com.countrychicken.commerce.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {
    Optional<ShoppingCart> findByUserId(UUID userId);
}
