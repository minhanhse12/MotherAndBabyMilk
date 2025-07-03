package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.PreOrder;
import com.motherandbabymilk.entity.PreOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreOrderRepository extends JpaRepository<PreOrder, Integer> {
    List<PreOrder> findByUserIdAndStatusNot(int userId, PreOrderStatus status);
    List<PreOrder> findByStatus(PreOrderStatus status);
}