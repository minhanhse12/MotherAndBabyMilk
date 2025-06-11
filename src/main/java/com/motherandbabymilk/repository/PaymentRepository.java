package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
