package com.countrychicken.permission.repository;

import com.countrychicken.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Permission> findByNameIgnoreCase(String name);
}
