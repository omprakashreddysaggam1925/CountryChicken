package com.countrychicken.commerce.dto;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import jakarta.validation.constraints.Size;
public record CheckoutRequest(@NotNull UUID addressId, @Size(max = 64) String couponCode) {}
