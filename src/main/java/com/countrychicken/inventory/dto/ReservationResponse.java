package com.countrychicken.inventory.dto;

import com.countrychicken.inventory.entity.StockReservation;

import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(UUID id, UUID variantId, int quantity, Instant expiresAt, String status) {
    public static ReservationResponse from(StockReservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getInventoryItem().getVariant().getId(),
                reservation.getQuantity(), reservation.getExpiresAt(), reservation.getStatus().name());
    }
}
