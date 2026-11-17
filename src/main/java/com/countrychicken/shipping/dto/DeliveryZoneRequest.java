package com.countrychicken.shipping.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DeliveryZoneRequest(
        @NotBlank @Size(max = 64) String code,
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 20) String postalCodePrefix,
        @NotNull @DecimalMin("0.00") BigDecimal shippingFee,
        @Min(1) int estimatedDeliveryDays,
        boolean active) {}
