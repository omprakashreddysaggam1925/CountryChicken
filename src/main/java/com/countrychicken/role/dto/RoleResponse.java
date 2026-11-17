package com.countrychicken.role.dto;

import com.countrychicken.role.entity.Role;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record RoleResponse(UUID id, String name, String description, Set<UUID> permissionIds) {
    public static RoleResponse from(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getPermissions().stream().map(permission -> permission.getId()).collect(Collectors.toUnmodifiableSet())
        );
    }
}
