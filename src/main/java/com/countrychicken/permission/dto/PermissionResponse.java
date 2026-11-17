package com.countrychicken.permission.dto;

import com.countrychicken.permission.entity.Permission;

import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String name,
        String resource,
        String operation,
        String description
) {
    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getResource(),
                permission.getOperation(),
                permission.getDescription()
        );
    }
}
