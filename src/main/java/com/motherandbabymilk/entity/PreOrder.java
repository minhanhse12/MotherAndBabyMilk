package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "preorder")
public class PreOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "total_price", nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    private int quantity;



    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreOrderStatus status;

    @Column(name = "vnpay_txn_ref")
    private String vnpayTxnRef;

    @Column
    private LocalDateTime confirmedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @PrePersist
    public void calculateTotalPrice() {
        this.totalAmount = this.unitPrice * this.quantity;
    }
}
