package com.countrychicken.shipping.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "delivery_zones")
public class DeliveryZone extends AbstractEntity {
    @Column(nullable = false, length = 64, unique = true) private String code;
    @Column(nullable = false, length = 150) private String name;
    @Column(name = "postal_code_prefix", nullable = false, length = 20) private String postalCodePrefix;
    @Column(name = "shipping_fee", nullable = false, precision = 12, scale = 2) private BigDecimal shippingFee;
    @Column(name = "estimated_delivery_days", nullable = false) private int estimatedDeliveryDays;
    @Column(nullable = false) private boolean active;

    protected DeliveryZone() {}

    public DeliveryZone(String code, String name, String postalCodePrefix, BigDecimal shippingFee,
                        int estimatedDeliveryDays, boolean active) {
        update(code, name, postalCodePrefix, shippingFee, estimatedDeliveryDays);
        this.active = active;
    }

    public void update(String code, String name, String postalCodePrefix, BigDecimal shippingFee, int estimatedDeliveryDays) {
        this.code = code;
        this.name = name;
        this.postalCodePrefix = postalCodePrefix;
        this.shippingFee = shippingFee;
        this.estimatedDeliveryDays = estimatedDeliveryDays;
    }

    public void setActive(boolean active) { this.active = active; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getPostalCodePrefix() { return postalCodePrefix; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public int getEstimatedDeliveryDays() { return estimatedDeliveryDays; }
    public boolean isActive() { return active; }
}
