package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    @Column(name = "is_delete", nullable = false)
    private boolean isDelete = false;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @PrePersist
    public void setOrderDate() {
        this.orderDate = LocalDateTime.now();
    }
}