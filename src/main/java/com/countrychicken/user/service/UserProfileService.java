package com.countrychicken.user.service;

import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.dto.UpdateProfileRequest;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserProfileService {

    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found"));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found"));
        String mobileNumber = normalizeOptional(request.mobileNumber());
        if (mobileNumber != null
                && !mobileNumber.equals(user.getMobileNumber())
                && userRepository.existsByMobileNumber(mobileNumber)) {
            throw new com.countrychicken.common.exception.ConflictException(
                    "An account already exists for this mobile number"
            );
        }
        user.updateProfile(
                mobileNumber,
                request.firstName() == null ? user.getFirstName() : request.firstName().trim(),
                request.lastName() == null ? user.getLastName() : normalizeOptional(request.lastName())
        );
        return UserResponse.from(userRepository.save(user));
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
