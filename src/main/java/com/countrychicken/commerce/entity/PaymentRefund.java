package com.countrychicken.commerce.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_refunds")
public class PaymentRefund extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "payment_id", nullable = false) private Payment payment;
    @Column(name = "provider_refund_id", unique = true, length = 128) private String providerRefundId;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private RefundStatus status;
    protected PaymentRefund() {}
    public PaymentRefund(Payment payment, BigDecimal amount) { this.payment = payment; this.amount = amount; this.status = RefundStatus.PENDING; }
    public void complete(String id) { this.providerRefundId = id; this.status = RefundStatus.COMPLETED; }
    public void fail() { this.status = RefundStatus.FAILED; }
    public Payment getPayment() { return payment; } public BigDecimal getAmount() { return amount; }
    public RefundStatus getStatus() { return status; } public String getProviderRefundId() { return providerRefundId; }
}
