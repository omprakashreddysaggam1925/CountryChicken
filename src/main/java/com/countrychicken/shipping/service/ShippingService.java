package com.countrychicken.shipping.service;

import com.countrychicken.commerce.entity.*;
import com.countrychicken.commerce.repository.CustomerOrderRepository;
import com.countrychicken.common.exception.*;
import com.countrychicken.shipping.dto.*;
import com.countrychicken.shipping.entity.*;
import com.countrychicken.shipping.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

@Service
public class ShippingService {
    private final DeliveryZoneRepository zones;
    private final ShipmentRepository shipments;
    private final CustomerOrderRepository orders;

    public ShippingService(DeliveryZoneRepository zones, ShipmentRepository shipments, CustomerOrderRepository orders) {
        this.zones = zones;
        this.shipments = shipments;
        this.orders = orders;
    }

    @Transactional(readOnly = true)
    public DeliveryZoneResponse quote(String postalCode) {
        return DeliveryZoneResponse.from(zoneFor(postalCode));
    }

    @Transactional
    public DeliveryZoneResponse createZone(DeliveryZoneRequest request) {
        if (zones.findByCode(request.code()).isPresent()) throw new ConflictException("Delivery zone code is already in use");
        return DeliveryZoneResponse.from(zones.save(new DeliveryZone(request.code(), request.name(),
                request.postalCodePrefix(), request.shippingFee(), request.estimatedDeliveryDays(), request.active())));
    }

    @Transactional(readOnly = true)
    public List<DeliveryZoneResponse> listZones() {
        return zones.findAll().stream().map(DeliveryZoneResponse::from).toList();
    }

    @Transactional
    public DeliveryZoneResponse updateZone(UUID zoneId, DeliveryZoneRequest request) {
        DeliveryZone zone = zones.findById(zoneId).orElseThrow(() -> new ResourceNotFoundException("Delivery zone was not found"));
        zones.findByCode(request.code()).filter(existing -> !existing.getId().equals(zoneId))
                .ifPresent(existing -> { throw new ConflictException("Delivery zone code is already in use"); });
        zone.update(request.code(), request.name(), request.postalCodePrefix(), request.shippingFee(), request.estimatedDeliveryDays());
        zone.setActive(request.active());
        return DeliveryZoneResponse.from(zone);
    }

    @Transactional
    public ShipmentResponse createForPaidOrder(UUID orderId) {
        Shipment existing = shipments.findByOrderId(orderId).orElse(null);
        if (existing != null) return ShipmentResponse.from(existing);
        CustomerOrder order = orders.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order was not found"));
        if (order.getStatus() != OrderStatus.PAID) throw new ConflictException("Only paid orders can be shipped");
        DeliveryZone zone = zoneFor(order.getDeliveryAddress().getPostalCode());
        Shipment shipment = new Shipment(order, zone, trackingNumber(),
                Instant.now().plusSeconds(zone.getEstimatedDeliveryDays() * 86_400L));
        order.markProcessing();
        return ShipmentResponse.from(shipments.save(shipment));
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getForCustomer(UUID userId, UUID orderId) {
        Shipment shipment = shipments.findByOrderId(orderId)
                .filter(value -> value.getOrder().getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Shipment was not found"));
        return ShipmentResponse.from(shipment);
    }

    @Transactional
    public ShipmentResponse updateStatus(UUID shipmentId, ShipmentStatus status) {
        Shipment shipment = shipments.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment was not found"));
        ShipmentStatus previous = shipment.getStatus();
        shipment.transitionTo(status);
        if (status == ShipmentStatus.SHIPPED && previous != ShipmentStatus.SHIPPED) shipment.getOrder().markShipped();
        if (status == ShipmentStatus.DELIVERED && previous != ShipmentStatus.DELIVERED) shipment.getOrder().markDelivered();
        return ShipmentResponse.from(shipment);
    }

    private DeliveryZone zoneFor(String postalCode) {
        return zones.findByActiveTrueOrderByPostalCodePrefixDesc().stream()
                .filter(zone -> postalCode.startsWith(zone.getPostalCodePrefix()))
                .findFirst()
                .orElseThrow(() -> new ConflictException("Delivery is not available for this postal code"));
    }

    private String trackingNumber() {
        return "CC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase(Locale.ROOT);
    }
}
