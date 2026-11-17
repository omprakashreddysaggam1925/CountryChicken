package com.countrychicken.user.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.user.dto.RegisterUserRequest;
import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.dto.VerifyEmailRequest;
import com.countrychicken.user.service.UserRegistrationService;
import com.countrychicken.user.service.VerificationTokenService;
import com.countrychicken.security.dto.LoginRequest;
import com.countrychicken.security.dto.LoginResponse;
import com.countrychicken.security.service.LoginService;
import com.countrychicken.security.dto.RefreshTokenRequest;
import com.countrychicken.security.dto.TokenPairResponse;
import com.countrychicken.security.service.RefreshTokenService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;
    private final VerificationTokenService verificationTokenService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    public UserRegistrationController(
            UserRegistrationService userRegistrationService,
            VerificationTokenService verificationTokenService,
            LoginService loginService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRegistrationService = userRegistrationService;
        this.verificationTokenService = verificationTokenService;
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletRequest servletRequest
    ) {
        UserResponse response = userRegistrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Registration completed", servletRequest.getRequestURI()));
    }

    @PostMapping("/verify-email/{userId}")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @PathVariable UUID userId,
            @Valid @RequestBody VerifyEmailRequest request,
            HttpServletRequest servletRequest
    ) {
        verificationTokenService.verifyEmail(userId, request.token());
        return ResponseEntity.ok(ApiResponse.success(null, "Email verified", servletRequest.getRequestURI()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest
    ) {
        LoginResponse response = loginService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful", servletRequest.getRequestURI()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenPairResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest servletRequest
    ) {
        TokenPairResponse response = refreshTokenService.rotate(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed", servletRequest.getRequestURI()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest servletRequest
    ) {
        refreshTokenService.revoke(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful", servletRequest.getRequestURI()));
    }
}
