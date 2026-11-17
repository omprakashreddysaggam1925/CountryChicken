package com.countrychicken.commerce.dto;
import com.countrychicken.commerce.entity.OrderItem;
import java.math.BigDecimal;
import java.util.UUID;
public record OrderItemResponse(UUID variantId, String sku, String productName, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {
    public static OrderItemResponse from(OrderItem item) { return new OrderItemResponse(item.getVariant().getId(), item.getSkuSnapshot(), item.getProductNameSnapshot(), item.getUnitPrice(), item.getQuantity(), item.getLineTotal()); }
}
