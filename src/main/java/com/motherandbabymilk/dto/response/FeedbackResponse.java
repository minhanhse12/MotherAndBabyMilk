package com.motherandbabymilk.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponse{
    private int id;
    private int userId;
    private int productId;
    private String productName; // Thêm tên sản phẩm để hiển thị
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Boolean isApproved;
}
