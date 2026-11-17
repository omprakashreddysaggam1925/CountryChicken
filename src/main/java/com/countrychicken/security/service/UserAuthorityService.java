package com.countrychicken.security.service;

import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserAuthorityService {

    private final UserRepository userRepository;

    public UserAuthorityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<String> authoritiesFor(UUID userId) {
        return userRepository.findPermissionNamesByUserId(userId);
    }
}
