package com.countrychicken.user.controller;

import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.entity.UserStatus;
import com.countrychicken.user.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserProfileControllerTest {

    private final UserProfileService service = mock(UserProfileService.class);
    private final UserProfileController controller = new UserProfileController(service);

    @Test
    void returnsAuthenticatedUserProfile() {
        UUID userId = UUID.randomUUID();
        when(service.getProfile(userId)).thenReturn(new UserResponse(
                userId,
                "customer@example.com",
                null,
                "Customer",
                null,
                UserStatus.ACTIVE
        ));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId.toString(),
                null,
                java.util.List.of()
        );
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/users/me");

        var response = controller.getMyProfile(authentication, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data().email()).isEqualTo("customer@example.com");
    }
}
