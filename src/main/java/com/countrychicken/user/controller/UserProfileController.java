package com.countrychicken.user.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import com.countrychicken.user.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            Authentication authentication,
            HttpServletRequest request
    ) {
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());
        UserResponse response = userProfileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile loaded", request.getRequestURI()));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest profileRequest,
            HttpServletRequest request
    ) {
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());
        UserResponse response = userProfileService.updateProfile(userId, profileRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile updated", request.getRequestURI()));
    }
}
