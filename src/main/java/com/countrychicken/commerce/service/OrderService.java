package com.countrychicken.commerce.service;

import com.countrychicken.commerce.dto.*;
import com.countrychicken.commerce.entity.*;
import com.countrychicken.commerce.repository.*;
import com.countrychicken.common.exception.*;
import com.countrychicken.inventory.dto.ReserveStockRequest;
import com.countrychicken.inventory.entity.StockReservation;
import com.countrychicken.inventory.repository.StockReservationRepository;
import com.countrychicken.inventory.service.InventoryService;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import com.countrychicken.shipping.service.ShippingService;
import com.countrychicken.promotion.service.PromotionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class OrderService {
    private final CustomerOrderRepository orders;
    private final ShoppingCartRepository carts;
    private final DeliveryAddressRepository addresses;
    private final UserRepository users;
    private final InventoryService inventory;
    private final StockReservationRepository reservations;
    private final ShippingService shipping;
    private final PromotionService promotions;

    public OrderService(CustomerOrderRepository orders, ShoppingCartRepository carts, DeliveryAddressRepository addresses,
                        UserRepository users, InventoryService inventory, StockReservationRepository reservations, ShippingService shipping, PromotionService promotions) {
        this.orders = orders; this.carts = carts; this.addresses = addresses; this.users = users;
        this.inventory = inventory; this.reservations = reservations; this.shipping = shipping; this.promotions = promotions;
    }

    @Transactional
    public OrderResponse checkout(UUID userId, CheckoutRequest request) {
        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User was not found"));
        DeliveryAddress address = addresses.findByIdAndUserId(request.addressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery address was not found"));
        ShoppingCart cart = carts.findByUserId(userId)
                .orElseThrow(() -> new ConflictException("Cart is empty"));
        if (cart.getItems().isEmpty()) throw new ConflictException("Cart is empty");

        var shippingQuote = shipping.quote(address.getPostalCode());
        BigDecimal merchandiseSubtotal = cart.getItems().stream()
                .map(item -> item.getVariant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = promotions.discount(userId, request.couponCode(), merchandiseSubtotal);
        CustomerOrder order = new CustomerOrder(user, address, "INR", shippingQuote.shippingFee(), discount,
                request.couponCode() == null ? null : request.couponCode().trim().toUpperCase(Locale.ROOT));
        Instant expiresAt = Instant.now().plusSeconds(30 * 60L);
        for (CartItem cartItem : cart.getItems()) {
            var reservationResponse = inventory.reserve(cartItem.getVariant().getId(),
                    new ReserveStockRequest(cartItem.getQuantity(), expiresAt));
            StockReservation reservation = reservations.findById(reservationResponse.id())
                    .orElseThrow(() -> new IllegalStateException("Created stock reservation was not found"));
            order.addItem(new OrderItem(order, cartItem.getVariant(), cartItem.getQuantity(), reservation));
        }
        cart.clear();
        CustomerOrder saved = orders.save(order);
        promotions.redeem(userId, request.couponCode(), saved, discount);
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list(UUID userId) {
        return orders.findByUserIdOrderByCreatedAtDesc(userId).stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(UUID userId, UUID orderId) {
        return OrderResponse.from(order(userId, orderId));
    }

    @Transactional
    public OrderResponse cancel(UUID userId, UUID orderId) {
        CustomerOrder order = order(userId, orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) return OrderResponse.from(order);
        order.cancel();
        order.getItems().forEach(item -> {
            if (item.getStockReservation().getStatus() == com.countrychicken.inventory.entity.ReservationStatus.ACTIVE) {
                inventory.release(item.getStockReservation().getId());
            }
        });
        return OrderResponse.from(order);
    }

    private CustomerOrder order(UUID userId, UUID orderId) {
        return orders.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order was not found"));
    }
}
