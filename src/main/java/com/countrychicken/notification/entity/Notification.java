package com.countrychicken.notification.entity;

import com.countrychicken.common.persistence.AbstractEntity;
import com.countrychicken.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private NotificationChannel channel;
    @Column(name = "notification_type", nullable = false, length = 64) private String type;
    @Column(nullable = false, length = 200) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String body;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private NotificationStatus status;
    @Column(nullable = false) private int attempts;
    @Column(name = "next_attempt_at") private Instant nextAttemptAt;
    @Column(name = "sent_at") private Instant sentAt;
    protected Notification() {}
    public Notification(User user, NotificationChannel channel, String type, String title, String body) {
        this.user = user; this.channel = channel; this.type = type; this.title = title; this.body = body;
        this.status = NotificationStatus.PENDING; this.nextAttemptAt = Instant.now();
    }
    public User getUser() { return user; } public NotificationChannel getChannel() { return channel; }
    public String getType() { return type; } public String getTitle() { return title; } public String getBody() { return body; }
    public NotificationStatus getStatus() { return status; } public int getAttempts() { return attempts; }
    public void markSent() { status = NotificationStatus.SENT; sentAt = Instant.now(); attempts++; }
    public void markFailed() { status = NotificationStatus.FAILED; attempts++; }
}
