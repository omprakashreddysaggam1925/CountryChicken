package com.countrychicken.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank @Email @Size(max = 320) String email,
        @Size(max = 32) String mobileNumber,
        @NotBlank @Size(min = 12, max = 128) String password,
        @NotBlank @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Size(max = 32) String referralCode
) {
    public RegisterUserRequest(String email, String mobileNumber, String password, String firstName, String lastName) {
        this(email, mobileNumber, password, firstName, lastName, null);
    }
}
