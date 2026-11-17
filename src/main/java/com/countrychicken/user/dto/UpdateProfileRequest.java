package com.countrychicken.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 32) String mobileNumber,
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName
) {
}
