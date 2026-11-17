package com.countrychicken.commerce.dto;
import jakarta.validation.constraints.*;
import java.util.UUID;
public record CartItemRequest(@NotNull UUID variantId, @NotNull @Min(1) @Max(100) Integer quantity) {}
