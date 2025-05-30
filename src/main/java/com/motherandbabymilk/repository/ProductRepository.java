package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.isDelete = false")
    Product findByName(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.isDelete = false")
    Optional<Product> findById(@Param("id") int id);

    @Query("SELECT p FROM Product p WHERE p.isDelete = false")
    List<Product> findAllNotDelete();
}