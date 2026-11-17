package com.countrychicken.user.controller;

import com.countrychicken.common.exception.GlobalExceptionHandler;
import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.entity.UserStatus;
import com.countrychicken.user.service.UserRegistrationService;
import com.countrychicken.user.service.VerificationTokenService;
import com.countrychicken.security.service.LoginService;
import com.countrychicken.security.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRegistrationControllerTest {

    private MockMvc mockMvc;
    private final UserRegistrationService service = mock(UserRegistrationService.class);
    private final VerificationTokenService verificationTokenService = mock(VerificationTokenService.class);
    private final LoginService loginService = mock(LoginService.class);
    private final RefreshTokenService refreshTokenService = mock(RefreshTokenService.class);
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserRegistrationController(
                        service,
                        verificationTokenService,
                        loginService,
                        refreshTokenService
                ))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registersValidRequest() throws Exception {
        UserResponse response = new UserResponse(
                UUID.randomUUID(),
                "customer@example.com",
                null,
                "Customer",
                null,
                UserStatus.PENDING_VERIFICATION
        );
        when(service.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "customer@example.com",
                                  "password": "very-secure-password",
                                  "firstName": "Customer"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("customer@example.com"))
                .andExpect(jsonPath("$.data.status").value("PENDING_VERIFICATION"));
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isArray());
    }
}
