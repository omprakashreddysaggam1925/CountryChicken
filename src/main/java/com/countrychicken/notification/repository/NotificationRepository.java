package com.countrychicken.notification.repository;

import com.countrychicken.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
