package com.countrychicken.commerce.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false, unique = true) private CustomerOrder order;
    @Column(nullable = false, length = 32) private String provider;
    @Column(name = "provider_order_id", unique = true, length = 128) private String providerOrderId;
    @Column(name = "provider_payment_id", unique = true, length = 128) private String providerPaymentId;
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 128) private String idempotencyKey;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 3) private String currency;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private PaymentStatus status;
    protected Payment() {}
    public Payment(CustomerOrder order, String provider, String idempotencyKey) {
        this.order = order; this.provider = provider; this.idempotencyKey = idempotencyKey;
        this.amount = order.getTotal(); this.currency = order.getCurrency(); this.status = PaymentStatus.CREATED;
    }
    public void providerOrder(String id) { this.providerOrderId = id; }
    public void capture(String paymentId) { this.providerPaymentId = paymentId; this.status = PaymentStatus.CAPTURED; }
    public void fail() { if (status != PaymentStatus.CAPTURED && status != PaymentStatus.REFUNDED) status = PaymentStatus.FAILED; }
    public void refund() { this.status = PaymentStatus.REFUNDED; }
    public CustomerOrder getOrder() { return order; } public String getProvider() { return provider; }
    public String getProviderOrderId() { return providerOrderId; } public String getProviderPaymentId() { return providerPaymentId; }
    public String getIdempotencyKey() { return idempotencyKey; } public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; } public PaymentStatus getStatus() { return status; }
}
