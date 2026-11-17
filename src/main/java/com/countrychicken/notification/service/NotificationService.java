package com.countrychicken.notification.service;

import com.countrychicken.common.exception.ResourceNotFoundException;
import com.countrychicken.notification.dto.NotificationResponse;
import com.countrychicken.notification.entity.*;
import com.countrychicken.notification.repository.NotificationRepository;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final UserRepository users;
    private final NotificationProvider provider;
    public NotificationService(NotificationRepository repository, UserRepository users, NotificationProvider provider) {
        this.repository = repository; this.users = users; this.provider = provider;
    }
    @Transactional
    public NotificationResponse send(UUID userId, NotificationChannel channel, String type, String title, String body) {
        var user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var notification = repository.save(new Notification(user, channel, type, title, body));
        provider.deliver(notification);
        notification.markSent();
        return NotificationResponse.from(notification);
    }
    @Transactional(readOnly = true)
    public List<NotificationResponse> list(UUID userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(NotificationResponse::from).collect(Collectors.toList());
    }
}
