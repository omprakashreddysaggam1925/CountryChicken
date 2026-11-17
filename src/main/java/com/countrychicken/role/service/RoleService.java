package com.countrychicken.role.service;

import com.countrychicken.common.exception.ConflictException;
import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.permission.entity.Permission;
import com.countrychicken.permission.repository.PermissionRepository;
import com.countrychicken.role.dto.CreateRoleRequest;
import com.countrychicken.role.dto.RoleResponse;
import com.countrychicken.role.dto.UpdateRolePermissionsRequest;
import com.countrychicken.role.entity.Role;
import com.countrychicken.role.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> list() {
        return roleRepository.findAll().stream().map(RoleResponse::from).toList();
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        String name = request.name().trim().toUpperCase(Locale.ROOT);
        if (roleRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A role already exists with this name");
        }
        return RoleResponse.from(roleRepository.save(new Role(name, normalize(request.description()))));
    }

    @Transactional
    public RoleResponse updatePermissions(UUID roleId, UpdateRolePermissionsRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role was not found"));
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.permissionIds()));
        if (permissions.size() != request.permissionIds().size()) {
            throw new ResourceNotFoundException("One or more permissions were not found");
        }
        role.replacePermissions(permissions);
        return RoleResponse.from(roleRepository.save(role));
    }

    @Transactional
    public void delete(UUID roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role was not found");
        }
        roleRepository.deleteById(roleId);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
