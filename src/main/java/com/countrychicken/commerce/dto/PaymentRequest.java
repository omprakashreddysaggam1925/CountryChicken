package com.countrychicken.commerce.dto;
import jakarta.validation.constraints.*;
import java.util.UUID;
public record PaymentRequest(@NotNull UUID orderId, @NotBlank @Size(max = 128) String idempotencyKey) {}
