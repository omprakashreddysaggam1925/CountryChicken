package com.countrychicken.user.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.user.dto.UpdateUserStatusRequest;
import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.dto.UpdateUserRolesRequest;
import com.countrychicken.user.service.UserAccountAdministrationService;
import com.countrychicken.user.service.UserRoleService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserAccountAdministrationService accountService;
    private final UserRoleService roleService;

    public AdminUserController(UserAccountAdministrationService accountService, UserRoleService roleService) {
        this.accountService = accountService;
        this.roleService = roleService;
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<ApiResponse<UserResponse>> changeStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request,
            HttpServletRequest servletRequest
    ) {
        UserResponse response = accountService.changeStatus(userId, request.status());
        return ResponseEntity.ok(ApiResponse.success(
                response,
                "User status updated",
                servletRequest.getRequestURI()
        ));
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<ApiResponse<UserResponse>> replaceRoles(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRolesRequest request,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                roleService.replaceRoles(userId, request),
                "User roles updated",
                servletRequest.getRequestURI()
        ));
    }
}
