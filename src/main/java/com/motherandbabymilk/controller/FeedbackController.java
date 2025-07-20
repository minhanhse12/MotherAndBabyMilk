package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.request.FeedbackRequest;
import com.motherandbabymilk.dto.request.FeedbackUpdate;
import com.motherandbabymilk.dto.response.FeedbackResponse;
import com.motherandbabymilk.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@SecurityRequirement(name = "api")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // Tạo feedback mới
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest request) {
        FeedbackResponse response = feedbackService.createFeedback(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/get/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable("id") int id) {
        FeedbackResponse response = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(response);
    }

    // Lấy tất cả feedback
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks() {
        List<FeedbackResponse> response = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(response);
    }

    // Lấy feedback theo user ID
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByUserId(@PathVariable("userId") int userId) {
        List<FeedbackResponse> response = feedbackService.getFeedbacksByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // Lấy feedback theo product ID
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByProductId(@PathVariable("productId") int productId) {
        List<FeedbackResponse> response = feedbackService.getFeedbacksByProductId(productId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable("id") int id,
            @Valid @RequestBody FeedbackUpdate request) {
        FeedbackResponse response = feedbackService.updateFeedback(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<FeedbackResponse> approveFeedback(@PathVariable("id") int id) {
        FeedbackResponse response = feedbackService.approveFeedback(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<String> deleteFeedback(@PathVariable("id") int id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.ok("Feedback deleted successfully");
    }


    @GetMapping("/product/{productId}/average-rating")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<Double> getAverageRatingByProductId(@PathVariable("productId") int productId) {
        Double averageRating = feedbackService.getAverageRatingByProductId(productId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }
}