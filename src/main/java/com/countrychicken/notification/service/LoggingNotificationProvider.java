package com.countrychicken.notification.service;

import com.countrychicken.notification.entity.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingNotificationProvider implements NotificationProvider {
    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationProvider.class);
    @Override public void deliver(Notification notification) {
        log.info("Notification queued for delivery: type={}, channel={}, user={}",
                notification.getType(), notification.getChannel(), notification.getUser().getId());
    }
}
