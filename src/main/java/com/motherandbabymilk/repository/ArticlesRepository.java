package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Article;
import com.motherandbabymilk.entity.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticlesRepository extends JpaRepository<Article, Integer> {
    @Query("SELECT p FROM Article p WHERE p.title = :title AND p.isDelete = false")
    Article findByTitle(@Param("title") String title);

    @Query("SELECT p FROM Article p WHERE p.isDelete = false")
    List<Article> findAllNotDelete();
}

