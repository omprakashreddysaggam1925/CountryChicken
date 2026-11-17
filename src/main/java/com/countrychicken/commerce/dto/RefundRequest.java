package com.countrychicken.commerce.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record RefundRequest(@NotNull @Positive BigDecimal amount) {}
