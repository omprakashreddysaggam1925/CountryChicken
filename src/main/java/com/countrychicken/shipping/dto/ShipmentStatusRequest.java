package com.countrychicken.shipping.dto;

import com.countrychicken.shipping.entity.ShipmentStatus;
import jakarta.validation.constraints.NotNull;

public record ShipmentStatusRequest(@NotNull ShipmentStatus status) {}
