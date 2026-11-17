package com.countrychicken.shipping.entity;

import com.countrychicken.commerce.entity.CustomerOrder;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class ShippingEntityTest {
    @Test
    void deliveryZoneRetainsFeeAndMatchingPrefix() {
        DeliveryZone zone = new DeliveryZone("NORTH", "North", "560", new BigDecimal("75.00"), 2, true);

        assertTrue("560001".startsWith(zone.getPostalCodePrefix()));
        assertEquals(new BigDecimal("75.00"), zone.getShippingFee());
        assertEquals(2, zone.getEstimatedDeliveryDays());
    }

    @Test
    void shipmentTracksForwardOnlyLifecycleAndTimestampsDelivery() {
        DeliveryZone zone = new DeliveryZone("NORTH", "North", "560", BigDecimal.ZERO, 2, true);
        Shipment shipment = new Shipment(new CustomerOrder(null, null, "INR"), zone, "CC-TRACKING", Instant.now().plusSeconds(3600));

        shipment.transitionTo(ShipmentStatus.PROCESSING);
        shipment.transitionTo(ShipmentStatus.SHIPPED);
        shipment.transitionTo(ShipmentStatus.DELIVERED);

        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
        assertNotNull(shipment.getShippedAt());
        assertNotNull(shipment.getDeliveredAt());
        assertThrows(IllegalStateException.class, () -> shipment.transitionTo(ShipmentStatus.PROCESSING));
    }
}
