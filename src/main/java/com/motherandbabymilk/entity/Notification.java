package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private Date createdAt;

    @Column(nullable = false)
    private Date updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source = Source.USER;

    public enum Source {
        ADMIN, USER
    }
}