package com.countrychicken.shipping.entity;

import com.countrychicken.commerce.entity.CustomerOrder;
import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "shipments")
public class Shipment extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private CustomerOrder order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_zone_id", nullable = false)
    private DeliveryZone deliveryZone;
    @Column(name = "tracking_number", nullable = false, length = 80, unique = true) private String trackingNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private ShipmentStatus status;
    @Column(name = "shipping_fee", nullable = false, precision = 12, scale = 2) private BigDecimal shippingFee;
    @Column(name = "estimated_delivery_at", nullable = false) private Instant estimatedDeliveryAt;
    @Column(name = "shipped_at") private Instant shippedAt;
    @Column(name = "delivered_at") private Instant deliveredAt;

    protected Shipment() {}

    public Shipment(CustomerOrder order, DeliveryZone deliveryZone, String trackingNumber, Instant estimatedDeliveryAt) {
        this.order = order;
        this.deliveryZone = deliveryZone;
        this.trackingNumber = trackingNumber;
        this.status = ShipmentStatus.CREATED;
        this.shippingFee = deliveryZone.getShippingFee();
        this.estimatedDeliveryAt = estimatedDeliveryAt;
    }

    public void transitionTo(ShipmentStatus next) {
        if (status == ShipmentStatus.CANCELLED || status == ShipmentStatus.DELIVERED) {
            throw new IllegalStateException("Shipment cannot transition from " + status);
        }
        if (next.ordinal() < status.ordinal() && next != ShipmentStatus.CANCELLED) {
            throw new IllegalStateException("Shipment status cannot move backwards");
        }
        status = next;
        if (next == ShipmentStatus.SHIPPED && shippedAt == null) shippedAt = Instant.now();
        if (next == ShipmentStatus.DELIVERED) {
            if (shippedAt == null) shippedAt = Instant.now();
            deliveredAt = Instant.now();
        }
    }

    public CustomerOrder getOrder() { return order; }
    public DeliveryZone getDeliveryZone() { return deliveryZone; }
    public String getTrackingNumber() { return trackingNumber; }
    public ShipmentStatus getStatus() { return status; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public Instant getEstimatedDeliveryAt() { return estimatedDeliveryAt; }
    public Instant getShippedAt() { return shippedAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
}
