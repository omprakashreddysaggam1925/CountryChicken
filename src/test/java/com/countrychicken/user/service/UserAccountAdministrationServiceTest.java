package com.countrychicken.user.service;

import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.entity.UserStatus;
import com.countrychicken.user.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserAccountAdministrationServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserAccountAdministrationService service =
            new UserAccountAdministrationService(userRepository);

    @Test
    void suspendsUser() {
        UUID userId = UUID.randomUUID();
        User user = new User("customer@example.com", null, "hash", "Customer", null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = service.changeStatus(userId, UserStatus.SUSPENDED);

        assertThat(response.status()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    void doesNotReactivateDeactivatedUser() {
        UUID userId = UUID.randomUUID();
        User user = new User("customer@example.com", null, "hash", "Customer", null);
        user.changeStatus(UserStatus.DEACTIVATED);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.changeStatus(userId, UserStatus.ACTIVE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A deactivated account cannot be reactivated");
    }
}
