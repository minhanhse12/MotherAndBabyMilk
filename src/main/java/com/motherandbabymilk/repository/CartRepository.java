package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(int userId, String status);
    Optional<Cart> findByUserId(int userId);
}