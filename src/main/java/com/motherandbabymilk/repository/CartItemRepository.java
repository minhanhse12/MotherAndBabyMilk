package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    Optional<CartItem> findByCartIdAndProductIdAndIsDeleteFalse(Long cartId, int productId);


    List<CartItem> findByCartIdAndIsDeleteFalse(Long cartId);
}