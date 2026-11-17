package com.countrychicken.user.service;

import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.role.entity.Role;
import com.countrychicken.role.repository.RoleRepository;
import com.countrychicken.user.dto.UpdateUserRolesRequest;
import com.countrychicken.user.dto.UserResponse;
import com.countrychicken.user.entity.User;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public UserResponse replaceRoles(UUID userId, UpdateUserRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User was not found"));
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.roleIds()));
        if (roles.size() != request.roleIds().size()) {
            throw new ResourceNotFoundException("One or more roles were not found");
        }
        user.replaceRoles(roles);
        return UserResponse.from(userRepository.save(user));
    }
}
