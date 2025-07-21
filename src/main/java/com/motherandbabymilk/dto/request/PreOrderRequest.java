package com.motherandbabymilk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PreOrderRequest {
    @Schema(description = "Product ID", example = "1")
    @Min(value = 1, message = "Product ID must be greater than 0")
    private int productId;

    @Schema(description = "Quantity to pre-order", example = "5")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

}