package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "status", nullable = false)
    private boolean status = true; // Thêm lại status

    @Column(name = "is_delete", nullable = false)
    private boolean isDelete = false;
}