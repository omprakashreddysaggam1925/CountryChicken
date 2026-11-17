package com.countrychicken.commerce.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "customer_orders")
public class CustomerOrder extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "delivery_address_id", nullable = false) private DeliveryAddress deliveryAddress;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private OrderStatus status;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal subtotal;
    @Column(name = "shipping_fee", nullable = false, precision = 12, scale = 2) private BigDecimal shippingFee;
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2) private BigDecimal discountAmount;
    @Column(name = "coupon_code", length = 64) private String couponCode;
    @Column(nullable = false, length = 3) private String currency;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private List<OrderItem> items = new ArrayList<>();

    protected CustomerOrder() {}
    public CustomerOrder(User user, DeliveryAddress deliveryAddress, String currency) {
        this(user, deliveryAddress, currency, BigDecimal.ZERO);
    }
    public CustomerOrder(User user, DeliveryAddress deliveryAddress, String currency, BigDecimal shippingFee) {
        this(user, deliveryAddress, currency, shippingFee, BigDecimal.ZERO, null);
    }
    public CustomerOrder(User user, DeliveryAddress deliveryAddress, String currency, BigDecimal shippingFee,
                         BigDecimal discountAmount, String couponCode) {
        this.user = user; this.deliveryAddress = deliveryAddress; this.status = OrderStatus.PENDING_PAYMENT;
        this.subtotal = BigDecimal.ZERO; this.shippingFee = shippingFee; this.discountAmount = discountAmount; this.couponCode = couponCode;
        this.currency = currency;
    }
    public void addItem(OrderItem item) { items.add(item); subtotal = subtotal.add(item.getLineTotal()); }
    public void cancel() {
        if (status != OrderStatus.PENDING_PAYMENT) throw new IllegalStateException("Only pending orders can be cancelled");
        status = OrderStatus.CANCELLED;
    }
    public void markPaid() {
        if (status != OrderStatus.PENDING_PAYMENT) throw new IllegalStateException("Only pending orders can be marked paid");
        status = OrderStatus.PAID;
    }
    public void markProcessing() {
        if (status != OrderStatus.PAID) throw new IllegalStateException("Only paid orders can enter processing");
        status = OrderStatus.PROCESSING;
    }
    public void markShipped() {
        if (status != OrderStatus.PROCESSING) throw new IllegalStateException("Only processing orders can be shipped");
        status = OrderStatus.SHIPPED;
    }
    public void markDelivered() {
        if (status != OrderStatus.SHIPPED) throw new IllegalStateException("Only shipped orders can be delivered");
        status = OrderStatus.DELIVERED;
    }
    public User getUser() { return user; } public DeliveryAddress getDeliveryAddress() { return deliveryAddress; }
    public OrderStatus getStatus() { return status; } public BigDecimal getSubtotal() { return subtotal; }
    public String getCurrency() { return currency; } public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public BigDecimal getShippingFee() { return shippingFee; }
    public BigDecimal getDiscountAmount() { return discountAmount; } public String getCouponCode() { return couponCode; }
    public BigDecimal getTotal() { return subtotal.add(shippingFee).subtract(discountAmount); }
}
