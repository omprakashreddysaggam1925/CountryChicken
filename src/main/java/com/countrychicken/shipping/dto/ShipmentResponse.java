package com.countrychicken.shipping.dto;

import com.countrychicken.shipping.entity.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ShipmentResponse(UUID id, UUID orderId, String trackingNumber, ShipmentStatus status,
                               String zoneCode, BigDecimal shippingFee, Instant estimatedDeliveryAt,
                               Instant shippedAt, Instant deliveredAt) {
    public static ShipmentResponse from(Shipment shipment) {
        return new ShipmentResponse(shipment.getId(), shipment.getOrder().getId(), shipment.getTrackingNumber(),
                shipment.getStatus(), shipment.getDeliveryZone().getCode(), shipment.getShippingFee(),
                shipment.getEstimatedDeliveryAt(), shipment.getShippedAt(), shipment.getDeliveredAt());
    }
}
