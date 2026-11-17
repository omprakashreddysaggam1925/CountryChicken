package com.countrychicken.shipping.dto;

import com.countrychicken.shipping.entity.DeliveryZone;
import java.math.BigDecimal;
import java.util.UUID;

public record DeliveryZoneResponse(UUID id, String code, String name, String postalCodePrefix,
                                   BigDecimal shippingFee, int estimatedDeliveryDays, boolean active) {
    public static DeliveryZoneResponse from(DeliveryZone zone) {
        return new DeliveryZoneResponse(zone.getId(), zone.getCode(), zone.getName(), zone.getPostalCodePrefix(),
                zone.getShippingFee(), zone.getEstimatedDeliveryDays(), zone.isActive());
    }
}
