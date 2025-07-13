package com.motherandbabymilk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {
    @Schema(description = "Product name", example = "Similac Mom")
    @NotBlank(message = "Name cannot be blank!")
    @Size(max = 100, message = "Name must be less than 100 characters!")
    private String name;

    @Schema(description = "Product image URL", example = "https://example.com/similac.jpg")
    @NotBlank(message = "Image URL cannot be blank!")
    private String image;

    @Schema(description = "Product price", example = "29.99")
    @Min(value = 0, message = "Price must be non-negative!")
    private double price;

    @Schema(description = "Product description", example = "Milk formula for pregnant women")
    @Size(max = 500, message = "Description must be less than 500 characters!")
    private String description;

    @Schema(description = "Category ID", example = "1")
    @Min(value = 1, message = "Category ID must be positive!")
    private int categoryId;

    @Schema(description = "Brand ID", example = "1")
    @Min(value = 1, message = "Brand ID must be positive!")
    private int brandId;

    @Schema(description = "Product quantity", example = "100")
    @Min(value = 0, message = "Quantity must be non-negative!")
    private int quantity;

    @Schema(description = "", example = "1")
    private int id;

    private boolean status;
}