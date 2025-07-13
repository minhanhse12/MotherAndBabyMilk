package com.motherandbabymilk.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class OrderItemRequest {
    private int productId;
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    private double price;
}
