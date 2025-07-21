package com.motherandbabymilk.controller;

import com.motherandbabymilk.entity.Notification;
import com.motherandbabymilk.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<NotificationService.NotificationResponse> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestAttribute("sub") String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is required");
        }
        return ResponseEntity.ok(notificationService.getNotifications(username, page, limit));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @RequestAttribute("sub") String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is required");
        }
        notificationService.markAsRead(id, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<NotificationService.NotificationResponse> getAllNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(notificationService.getAllNotifications(page, limit));
    }

    @PostMapping("/{id}/read-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> markAsReadAdmin(@PathVariable Long id) {
        notificationService.markAsReadAdmin(id);
        return ResponseEntity.ok().build();
    }
}