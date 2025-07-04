package com.motherandbabymilk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponse{
    private int id;
    private int userId;
    private String fullName;
    private int productId;
    private String productName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Boolean isApproved;
}
