package com.countrychicken.inventory.service;

import com.countrychicken.common.exception.ConflictException;
import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.inventory.dto.*;
import com.countrychicken.inventory.entity.*;
import com.countrychicken.inventory.repository.*;
import com.countrychicken.product.entity.ProductVariant;
import com.countrychicken.product.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class InventoryService {
    private final InventoryItemRepository items;
    private final InventoryMovementRepository movements;
    private final StockReservationRepository reservations;
    private final ProductVariantRepository variants;

    public InventoryService(InventoryItemRepository items, InventoryMovementRepository movements,
                            StockReservationRepository reservations, ProductVariantRepository variants) {
        this.items = items;
        this.movements = movements;
        this.reservations = reservations;
        this.variants = variants;
    }

    @Transactional
    public InventoryResponse adjust(UUID variantId, InventoryAdjustmentRequest request) {
        ProductVariant variant = variant(variantId);
        InventoryItem item = items.findByVariantIdForUpdate(variantId).orElseGet(
                () -> items.save(new InventoryItem(variant, request.lowStockThreshold() == null ? 0 : request.lowStockThreshold())));
        item.adjustOnHand(request.quantityDelta());
        if (request.lowStockThreshold() != null) {
            item.updateThreshold(request.lowStockThreshold());
        }
        movements.save(new InventoryMovement(item, request.movementType(), Math.abs(request.quantityDelta()),
                null, normalize(request.reason())));
        return InventoryResponse.from(item);
    }

    @Transactional
    public ReservationResponse reserve(UUID variantId, ReserveStockRequest request) {
        ProductVariant variant = variant(variantId);
        InventoryItem item = items.findByVariantIdForUpdate(variantId).orElseThrow(
                () -> new ConflictException("No inventory has been configured for this variant"));
        expireReservations(item);
        item.reserve(request.quantity());
        StockReservation reservation = reservations.save(
                new StockReservation(item, request.quantity(), request.expiresAt()));
        movements.save(new InventoryMovement(item, InventoryMovementType.RESERVATION, request.quantity(),
                reservation.getId(), "Stock reserved"));
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse release(UUID reservationId) {
        StockReservation reservation = reservation(reservationId);
        InventoryItem item = items.findByVariantIdForUpdate(reservation.getInventoryItem().getVariant().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item was not found"));
        StockReservation locked = reservations.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock reservation was not found"));
        if (locked.getStatus() != ReservationStatus.ACTIVE) {
            return ReservationResponse.from(locked);
        }
        item.release(locked.getQuantity());
        if (locked.getExpiresAt().isBefore(Instant.now())) {
            locked.expire();
        } else {
            locked.release();
        }
        movements.save(new InventoryMovement(item, InventoryMovementType.RELEASE, locked.getQuantity(),
                locked.getId(), "Stock reservation released"));
        return ReservationResponse.from(locked);
    }

    @Transactional
    public ReservationResponse commit(UUID reservationId) {
        StockReservation reservation = reservation(reservationId);
        InventoryItem item = items.findByVariantIdForUpdate(reservation.getInventoryItem().getVariant().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item was not found"));
        StockReservation locked = reservations.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock reservation was not found"));
        if (locked.getStatus() != ReservationStatus.ACTIVE) {
            throw new ConflictException("Only an active stock reservation can be committed");
        }
        if (locked.getExpiresAt().isBefore(Instant.now())) {
            item.release(locked.getQuantity());
            locked.expire();
            movements.save(new InventoryMovement(item, InventoryMovementType.RELEASE, locked.getQuantity(),
                    locked.getId(), "Expired stock reservation"));
            throw new ConflictException("Stock reservation has expired");
        }
        item.commit(locked.getQuantity());
        locked.commit();
        movements.save(new InventoryMovement(item, InventoryMovementType.COMMIT, locked.getQuantity(),
                locked.getId(), "Stock reservation committed"));
        return ReservationResponse.from(locked);
    }

    @Transactional(readOnly = true)
    public InventoryResponse get(UUID variantId) {
        return InventoryResponse.from(items.findByVariantId(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item was not found")));
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> lowStock() {
        return items.findAll().stream()
                .filter(item -> item.getAvailable() <= item.getLowStockThreshold())
                .map(InventoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MovementResponse> movements(UUID variantId) {
        InventoryItem item = items.findByVariantId(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item was not found"));
        return movements.findByInventoryItemIdOrderByCreatedAtDesc(item.getId()).stream()
                .map(MovementResponse::from).toList();
    }

    private ProductVariant variant(UUID id) {
        return variants.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product variant was not found"));
    }

    private StockReservation reservation(UUID id) {
        return reservations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stock reservation was not found"));
    }

    private void expireReservations(InventoryItem item) {
        for (StockReservation reservation : reservations.findExpiredForUpdate(item.getId(), Instant.now())) {
            item.release(reservation.getQuantity());
            reservation.expire();
            movements.save(new InventoryMovement(item, InventoryMovementType.RELEASE, reservation.getQuantity(),
                    reservation.getId(), "Expired stock reservation"));
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
