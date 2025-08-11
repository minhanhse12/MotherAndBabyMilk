package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Order;
import com.motherandbabymilk.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByIsDeleteFalse();
    Optional<Order> findByVnpayTxnRef(String vnpayTxnRef);
    List<Order> findAllByStatus(OrderStatus status);
    Optional<Order> findByIdAndIsDeleteFalse(int id);
    List<Order> findByUserIdAndIsDeleteFalse(int userId);
    List<Order> findByUserIdAndIsDeleteFalseOrderByOrderDateDesc(int userId);

}
