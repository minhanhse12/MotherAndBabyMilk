package com.motherandbabymilk.dto;

import com.motherandbabymilk.entity.Notification;
import lombok.Data;

@Data
public class NotificationPayload {
    private String username;
    private String message;
    private Notification.Source source;
    private Long notificationId;

    public NotificationPayload(String username, String message, Notification.Source source, Long notificationId) {
        this.username = username;
        this.message = message;
        this.source = source;
        this.notificationId = notificationId;
    }
}