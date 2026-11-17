package com.countrychicken.commerce.dto;
import com.countrychicken.commerce.entity.CartItem;
import java.math.BigDecimal;
import java.util.UUID;
public record CartItemResponse(UUID variantId, String sku, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
    public static CartItemResponse from(CartItem i) { BigDecimal price = i.getVariant().getPrice(); return new CartItemResponse(i.getVariant().getId(), i.getVariant().getSku(), i.getQuantity(), price, price.multiply(BigDecimal.valueOf(i.getQuantity()))); }
}
