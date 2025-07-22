package com.motherandbabymilk.dto.response;

import com.motherandbabymilk.entity.PreOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PreOrderResponse {
    @Schema(description = "Pre-order ID", example = "1")
    private int id;

    @Schema(description = "User ID", example = "1")
    private int userId;

    @Schema(description = "Product ID", example = "1")
    private int productId;

    private double unitPrice;
    private double totalAmount;
    private String image;

    @Schema(description = "Product name", example = "Organic Milk")
    private String productName;

    @Schema(description = "Quantity", example = "5")
    private int quantity;

    @Schema(description = "Status", example = "PENDING")
    private PreOrderStatus status;

    @Schema(description = "Created at", example = "2025-06-28T09:37:00")
    private LocalDateTime createdAt;

    @Schema(description = "Confirmed at", example = "2025-06-28T10:00:00")
    private LocalDateTime confirmedAt;

}