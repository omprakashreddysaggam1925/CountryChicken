package com.countrychicken.commerce.dto;
import com.countrychicken.commerce.entity.ShoppingCart;
import java.math.BigDecimal;
import java.util.*;
public record CartResponse(UUID id, List<CartItemResponse> items, BigDecimal subtotal) {
    public static CartResponse from(ShoppingCart cart) { List<CartItemResponse> items = cart.getItems().stream().map(CartItemResponse::from).toList(); return new CartResponse(cart.getId(), items, items.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add)); }
}
