package com.countrychicken.role.controller;

import com.countrychicken.common.response.ApiResponse;
import com.countrychicken.role.dto.CreateRoleRequest;
import com.countrychicken.role.dto.RoleResponse;
import com.countrychicken.role.dto.UpdateRolePermissionsRequest;
import com.countrychicken.role.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> list(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(roleService.list(), "Roles retrieved", request.getRequestURI()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @Valid @RequestBody CreateRoleRequest body,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(roleService.create(body), "Role created", request.getRequestURI()));
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> updatePermissions(
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateRolePermissionsRequest body,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                roleService.updatePermissions(roleId, body),
                "Role permissions updated",
                request.getRequestURI()
        ));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID roleId) {
        roleService.delete(roleId);
        return ResponseEntity.noContent().build();
    }
}
