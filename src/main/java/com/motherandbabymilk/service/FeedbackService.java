package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.FeedbackRequest;
import com.motherandbabymilk.dto.request.FeedbackUpdate;
import com.motherandbabymilk.dto.response.FeedbackResponse;
import com.motherandbabymilk.entity.Feedback;
import com.motherandbabymilk.entity.Product;
import com.motherandbabymilk.exception.DuplicateProductException;
import com.motherandbabymilk.exception.ResourceNotFoundException;
import com.motherandbabymilk.repository.FeedbackRepository;
import com.motherandbabymilk.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {


    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ProductRepository productRepository;

    // Tạo feedback mới
    public FeedbackResponse createFeedback(FeedbackRequest requestDTO) {
        // Kiểm tra xem user đã feedback cho product này chưa
        Optional<Feedback> existingFeedback = feedbackRepository
                .findByUserIdAndProductId(requestDTO.getUserId(), requestDTO.getProductId());

        if (existingFeedback.isPresent()) {
            throw new DuplicateProductException("User has already provided feedback for this product");
        }

        // Kiểm tra product có tồn tại không
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + requestDTO.getProductId()));

        // Tạo feedback mới
        Feedback feedback = new Feedback();
        feedback.setUserId(requestDTO.getUserId());
        feedback.setProduct(product);
        feedback.setRating(requestDTO.getRating());
        feedback.setComment(requestDTO.getComment());
        feedback.setApproved(false); // Mặc định chưa được approve

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return mapToResponseDTO(savedFeedback);
    }

    // Lấy feedback theo ID
    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackById(int id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        return mapToResponseDTO(feedback);
    }

    // Lấy tất cả feedback
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return feedbacks.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Lấy feedback theo user ID
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksByUserId(int userId) {
        List<Feedback> feedbacks = feedbackRepository.findByUserId(userId);
        return feedbacks.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Lấy feedback theo product ID
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksByProductId(int productId) {
        List<Feedback> feedbacks = feedbackRepository.findByProductId(productId);
        return feedbacks.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Lấy feedback đã được approve theo product ID
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getApprovedFeedbacksByProductId(int productId) {
        List<Feedback> feedbacks = feedbackRepository.findByProductIdAndIsApproved(productId, true);
        return feedbacks.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Lấy feedback chưa được approve
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getPendingFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findByIsApprovedFalse();
        return feedbacks.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Cập nhật feedback
    public FeedbackResponse updateFeedback(int id, FeedbackUpdate updateDTO) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));

        // Cập nhật các trường nếu có giá trị mới
        if (updateDTO.getRating() != null) {
            feedback.setRating(updateDTO.getRating());
        }
        if (updateDTO.getComment() != null) {
            feedback.setComment(updateDTO.getComment());
        }
        if (updateDTO.getIsApproved() != null) {
            feedback.setApproved(updateDTO.getIsApproved());
        }

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return mapToResponseDTO(updatedFeedback);
    }

    // Approve feedback
    public FeedbackResponse approveFeedback(int id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));

        feedback.setApproved(true);
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return mapToResponseDTO(updatedFeedback);
    }

    // Reject feedback
    public FeedbackResponse rejectFeedback(int id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));

        feedback.setApproved(false);
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return mapToResponseDTO(updatedFeedback);
    }

    // Xóa feedback
    public void deleteFeedback(int id) {
        if (!feedbackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback not found with id: " + id);
        }
        feedbackRepository.deleteById(id);
    }

    // Lấy rating trung bình của product
    @Transactional(readOnly = true)
    public Double getAverageRatingByProductId(int productId) {
        return feedbackRepository.findAverageRatingByProductId(productId);
    }

    // Helper method để convert entity sang DTO
    private FeedbackResponse mapToResponseDTO(Feedback feedback) {
        FeedbackResponse dto = new FeedbackResponse();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUserId());
        dto.setProductId(feedback.getProduct().getId());
        dto.setProductName(feedback.getProduct().getName()); // Giả sử Product có field name
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setCreatedAt(feedback.getCreatedAt());
        dto.setIsApproved(feedback.isApproved());
        return dto;
    }
}