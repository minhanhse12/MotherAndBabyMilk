package com.motherandbabymilk.repository;

import com.motherandbabymilk.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    // Tìm feedback theo user ID
    List<Feedback> findByUserId(int userId);

    // Tìm feedback theo product ID
    List<Feedback> findByProductId(int productId);

    // Tìm feedback đã được approve
    List<Feedback> findByIsApproved(Boolean isApproved);

    // Tìm feedback theo product và trạng thái approve
    List<Feedback> findByProductIdAndIsApproved(int productId, Boolean isApproved);

    // Tìm feedback theo user và product
    Optional<Feedback> findByUserIdAndProductId(int userId, int productId);

    // Tính rating trung bình của product
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") int productId);

    // Đếm số lượng feedback theo rating
    @Query("SELECT f.rating, COUNT(f) FROM Feedback f WHERE f.product.id = :productId AND f.isApproved = true GROUP BY f.rating")
    List<Object[]> countFeedbackByRating(@Param("productId") int productId);

    // Tìm feedback chưa được approve
    List<Feedback> findByIsApprovedFalse();
}

