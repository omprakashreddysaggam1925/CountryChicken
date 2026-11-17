package com.countrychicken.user.service;

import com.countrychicken.user.dto.UpdateProfileRequest;
import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserProfileUpdateTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserProfileService service = new UserProfileService(userRepository);

    @Test
    void updatesProfileFieldsWithoutChangingEmail() {
        UUID userId = UUID.randomUUID();
        User user = new User("customer@example.com", null, "hash", "Old", "Name");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = service.updateProfile(
                userId,
                new UpdateProfileRequest("12345", "New", "Name")
        );

        assertThat(response.email()).isEqualTo("customer@example.com");
        assertThat(response.firstName()).isEqualTo("New");
        assertThat(response.mobileNumber()).isEqualTo("12345");
    }
}
