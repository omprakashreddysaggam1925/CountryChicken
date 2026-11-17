package com.countrychicken.permission.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.permission.dto.CreatePermissionRequest;
import com.countrychicken.permission.dto.PermissionResponse;
import com.countrychicken.permission.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> list(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                permissionService.list(),
                "Permissions retrieved",
                request.getRequestURI()
        ));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public ResponseEntity<ApiResponse<PermissionResponse>> create(
            @Valid @RequestBody CreatePermissionRequest body,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(permissionService.create(body), "Permission created", request.getRequestURI()));
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID permissionId) {
        permissionService.delete(permissionId);
        return ResponseEntity.noContent().build();
    }
}
