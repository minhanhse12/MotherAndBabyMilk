package com.motherandbabymilk.service;

import com.motherandbabymilk.config.NotificationWebSocketHandler;
import com.motherandbabymilk.dto.NotificationPayload;
import com.motherandbabymilk.entity.Notification;
import com.motherandbabymilk.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationWebSocketHandler webSocketHandler;

    @Data
    public static class NotificationResponse {
        private List<Notification> notifications;
        private long total;
        private long unreadCount;
    }

    public void sendNotification(NotificationPayload payload) throws IOException {
        Notification notification = new Notification();
        notification.setUsername(payload.getUsername());
        notification.setMessage(payload.getMessage());
        notification.setSource(payload.getSource());
        notification.setRead(false);
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());
        notificationRepository.save(notification);

        payload.setNotificationId(notification.getId());
        webSocketHandler.sendNotification(payload);
    }

    public NotificationResponse getNotifications(String username, int page, int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Notification> notificationPage = notificationRepository.findByUsernameAndSource(username, Notification.Source.ADMIN, pageRequest);
        long unreadCount = notificationRepository.countByUsernameAndSourceAndIsReadFalse(username, Notification.Source.ADMIN);

        NotificationResponse response = new NotificationResponse();
        response.setNotifications(notificationPage.getContent());
        response.setTotal(notificationPage.getTotalElements());
        response.setUnreadCount(unreadCount);
        return response;
    }

    public NotificationResponse getAllNotifications(int page, int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Notification> notificationPage = notificationRepository.findBySource(Notification.Source.USER, pageRequest);
        long unreadCount = notificationRepository.countBySourceAndIsReadFalse(Notification.Source.USER);

        NotificationResponse response = new NotificationResponse();
        response.setNotifications(notificationPage.getContent());
        response.setTotal(notificationPage.getTotalElements());
        response.setUnreadCount(unreadCount);
        return response;
    }

    public void markAsRead(Long id, String username) {
        Notification notification = notificationRepository.findByIdAndUsernameAndSource(id, username, Notification.Source.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Notification with ID " + id + " not found"));
        notification.setRead(true);
        notification.setUpdatedAt(new Date());
        notificationRepository.save(notification);
    }

    public void markAsReadAdmin(Long id) {
        Notification notification = notificationRepository.findByIdAndSource(id, Notification.Source.USER)
                .orElseThrow(() -> new EntityNotFoundException("Notification with ID " + id + " not found"));
        notification.setRead(true);
        notification.setUpdatedAt(new Date());
        notificationRepository.save(notification);
    }
}