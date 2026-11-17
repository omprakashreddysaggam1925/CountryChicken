package com.countrychicken.user.dto;

import com.countrychicken.user.entity.User;
import com.countrychicken.user.entity.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String mobileNumber,
        String firstName,
        String lastName,
        UserStatus status
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus()
        );
    }
}
