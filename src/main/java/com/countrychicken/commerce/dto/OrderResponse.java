package com.countrychicken.commerce.dto;
import com.countrychicken.commerce.entity.*;
import java.math.BigDecimal;
import java.util.*;
public record OrderResponse(UUID id, OrderStatus status, BigDecimal subtotal, BigDecimal shippingFee, BigDecimal discountAmount, BigDecimal total,
                            String currency, UUID addressId, List<OrderItemResponse> items) {
    public static OrderResponse from(CustomerOrder order) { return new OrderResponse(order.getId(), order.getStatus(),
            order.getSubtotal(), order.getShippingFee(), order.getDiscountAmount(), order.getTotal(), order.getCurrency(),
            order.getDeliveryAddress().getId(), order.getItems().stream().map(OrderItemResponse::from).toList()); }
}
