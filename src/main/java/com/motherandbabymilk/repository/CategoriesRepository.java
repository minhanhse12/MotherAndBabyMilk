package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
    @Query("SELECT p FROM Categories p WHERE p.name = :name AND p.isDelete = false")
    Categories findByName(@Param("name") String name);

    @Query("SELECT p FROM Categories p WHERE p.id = :id AND p.isDelete = false")
    Optional<Categories> findById(@Param("id") int id);

    @Query("SELECT p FROM Categories p WHERE p.isDelete = false")
    List<Categories> findAllNotDelete();
}

