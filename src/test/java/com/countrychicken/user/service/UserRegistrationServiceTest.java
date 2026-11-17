package com.countrychicken.user.service;

import com.countrychicken.common.exception.ConflictException;
import com.countrychicken.user.dto.RegisterUserRequest;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRegistrationServiceTest {

    private final UserRepository userRepository = org.mockito.Mockito.mock(UserRepository.class);
    private final VerificationTokenService verificationTokenService =
            org.mockito.Mockito.mock(VerificationTokenService.class);
    private final UserRegistrationService service = new UserRegistrationService(
            userRepository,
            new BCryptPasswordEncoder(),
            verificationTokenService
    );

    @Test
    void registersUserWithNormalizedEmailAndEncodedPassword() {
        RegisterUserRequest request = new RegisterUserRequest(
                "  CUSTOMER@EXAMPLE.COM ",
                " 12345 ",
                "very-secure-password",
                "Customer",
                "Example"
        );
        when(userRepository.existsByEmailIgnoreCase("customer@example.com")).thenReturn(false);
        when(userRepository.existsByMobileNumber("12345")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("customer@example.com");
        assertThat(saved.getMobileNumber()).isEqualTo("12345");
        assertThat(saved.getPasswordHash()).isNotEqualTo(request.password());
        assertThat(new BCryptPasswordEncoder().matches(request.password(), saved.getPasswordHash()))
                .isTrue();
    }

    @Test
    void rejectsDuplicateEmail() {
        RegisterUserRequest request = new RegisterUserRequest(
                "customer@example.com",
                null,
                "very-secure-password",
                "Customer",
                null
        );
        when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("An account already exists for this email");
    }
}
