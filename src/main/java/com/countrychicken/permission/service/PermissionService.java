package com.countrychicken.permission.service;

import com.countrychicken.common.exception.ConflictException;
import com.countrychicken.permission.dto.CreatePermissionRequest;
import com.countrychicken.permission.dto.PermissionResponse;
import com.countrychicken.permission.entity.Permission;
import com.countrychicken.permission.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class PermissionService {

    private final PermissionRepository repository;

    public PermissionService(PermissionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> list() {
        return repository.findAll().stream().map(PermissionResponse::from).toList();
    }

    @Transactional
    public PermissionResponse create(CreatePermissionRequest request) {
        String name = request.name().trim().toUpperCase(Locale.ROOT);
        if (repository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A permission already exists with this name");
        }
        String operation = request.operation().trim().toUpperCase(Locale.ROOT);
        if (!SetOfOperations.contains(operation)) {
            throw new IllegalArgumentException("Permission operation must be CREATE, READ, UPDATE, or DELETE");
        }
        return PermissionResponse.from(repository.save(new Permission(
                name,
                request.resource().trim().toUpperCase(Locale.ROOT),
                operation,
                normalize(request.description())
        )));
    }

    @Transactional
    public void delete(UUID permissionId) {
        if (!repository.existsById(permissionId)) {
            throw new com.countrychicken.common.exception.ResourceNotFoundException("Permission was not found");
        }
        repository.deleteById(permissionId);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static final class SetOfOperations {
        private static boolean contains(String value) {
            return value.equals("CREATE") || value.equals("READ") || value.equals("UPDATE") || value.equals("DELETE");
        }
    }
}
