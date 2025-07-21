package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Order;
import com.motherandbabymilk.entity.PreOrder;
import com.motherandbabymilk.entity.PreOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreOrderRepository extends JpaRepository<PreOrder, Integer> {
    List<PreOrder> findByUserIdAndStatusNot(int userId, PreOrderStatus status);
    List<PreOrder> findByStatus(PreOrderStatus status);
    Optional<PreOrder> findByVnpayTxnRef(String vnpayTxnRef);
    boolean existsByUserIdAndProductIdAndStatus(int userId, int productId, PreOrderStatus status);
}