package com.countrychicken.notification.dto;

import com.countrychicken.notification.entity.Notification;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, String channel, String type, String title, String body, String status, Instant createdAt) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getChannel().name(), n.getType(), n.getTitle(), n.getBody(), n.getStatus().name(), n.getCreatedAt());
    }
}
