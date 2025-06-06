package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image")
    private String image;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;;

    @Column(name = "is_delete", nullable = false)
    private boolean isDelete = false;

    @PrePersist
    public void setCreatedDate() {
        this.createdDate = LocalDateTime.now();
    }
}