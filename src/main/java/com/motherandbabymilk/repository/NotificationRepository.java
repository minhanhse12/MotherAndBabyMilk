package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUsernameAndSource(String username, Notification.Source source, Pageable pageable);
    Page<Notification> findBySource(Notification.Source source, Pageable pageable);
    long countByUsernameAndSourceAndIsReadFalse(String username, Notification.Source source);
    long countBySourceAndIsReadFalse(Notification.Source source);
    Optional<Notification> findByIdAndUsernameAndSource(Long id, String username, Notification.Source source);
    Optional<Notification> findByIdAndSource(Long id, Notification.Source source);
}