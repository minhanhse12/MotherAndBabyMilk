package com.motherandbabymilk.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CartRequest {

    @NotNull(message = "Product ID cannot be null")
    private Integer productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private String note;
}