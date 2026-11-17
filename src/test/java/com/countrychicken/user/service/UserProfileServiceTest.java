package com.countrychicken.user.service;

import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserProfileServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserProfileService service = new UserProfileService(userRepository);

    @Test
    void returnsSafeProfileProjection() {
        UUID userId = UUID.randomUUID();
        User user = new User("customer@example.com", null, "secret-hash", "Customer", "Example");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = service.getProfile(userId);

        assertThat(response.email()).isEqualTo("customer@example.com");
        assertThat(response.firstName()).isEqualTo("Customer");
        assertThat(response).hasOnlyFields("id", "email", "mobileNumber", "firstName", "lastName", "status");
    }
}
