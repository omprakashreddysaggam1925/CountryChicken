package com.countrychicken.user.service;

import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.entity.UserStatus;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserAccountAdministrationService {

    private final UserRepository userRepository;

    public UserAccountAdministrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse changeStatus(UUID userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found"));
        user.changeStatus(status);
        return UserResponse.from(userRepository.save(user));
    }
}
