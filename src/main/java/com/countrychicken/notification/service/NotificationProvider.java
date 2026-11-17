package com.countrychicken.notification.service;

import com.countrychicken.notification.entity.Notification;

public interface NotificationProvider {
    void deliver(Notification notification);
}
