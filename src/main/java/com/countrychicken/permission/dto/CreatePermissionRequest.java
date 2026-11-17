package com.countrychicken.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 100) String resource,
        @NotBlank @Size(max = 32) String operation,
        @Size(max = 500) String description
) {
}
