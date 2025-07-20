package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@ToString(exclude = {"order"})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "transaction_code", nullable = false)
    private String transactionCode;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, COMPLETED, FAILED

    @PrePersist
    public void setPaymentDate() {
        this.paymentDate = LocalDateTime.now();
    }
}