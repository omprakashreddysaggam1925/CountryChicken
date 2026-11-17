package com.countrychicken.user.repository;

import com.countrychicken.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMobileNumber(String mobileNumber);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("""
            select distinct permission.name
            from User user
            join user.roles role
            join role.permissions permission
            where user.id = :userId
              and user.status = com.countrychicken.user.entity.UserStatus.ACTIVE
            """)
    List<String> findPermissionNamesByUserId(@Param("userId") UUID userId);
}
