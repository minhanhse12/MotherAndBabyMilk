package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private double totalPrice;

    @Column
    private String note;

    @Column(nullable = false)
    private java.time.LocalDateTime addedAt;

    @Column(nullable = false)
    private boolean isSelect = true;  // Mặc định là được chọn

    @Column(nullable = false)
    private boolean isDelete = false; // Mặc định là chưa bị xóa
}
