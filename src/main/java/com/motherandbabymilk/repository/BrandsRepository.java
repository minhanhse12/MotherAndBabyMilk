package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandsRepository extends JpaRepository<Brands, Integer> {
    @Query("SELECT p FROM Brands p WHERE p.name = :name AND p.isDelete = false")
    Brands findByName(@Param("name") String name);

    @Query("SELECT p FROM Brands p WHERE p.id = :id AND p.isDelete = false")
    Optional<Brands> findById(@Param("id") int id);

    @Query("SELECT p FROM Brands p WHERE p.isDelete = false")
    List<Brands> findAllNotDelete();
}

