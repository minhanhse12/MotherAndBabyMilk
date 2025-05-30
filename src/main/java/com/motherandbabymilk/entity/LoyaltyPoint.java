package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class LoyaltyPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int points; // Số điểm cộng hoặc trừ

    @Column(nullable = false)
    private String type; // earn, redeem, adjust

    @Column
    private String description; // Ví dụ: "Mua đơn hàng #123", "Đổi voucher 50k", "Tặng sinh nhật"

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
