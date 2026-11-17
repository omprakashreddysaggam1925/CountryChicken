package com.countrychicken.inventory.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;

public record ReserveStockRequest(@Positive int quantity, @NotNull @Future Instant expiresAt) {}
