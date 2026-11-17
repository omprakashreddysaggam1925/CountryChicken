package com.countrychicken.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record VariantRequest(
        @NotBlank @Size(max = 100) String sku,
        @Positive int weightGrams,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal price,
        Boolean active
) {}
