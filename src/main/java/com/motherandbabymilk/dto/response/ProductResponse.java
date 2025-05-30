package com.motherandbabymilk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductResponse {
    @Schema(description = "Product ID", example = "1")
    private int id;

    @Schema(description = "Product name", example = "Similac Mom")
    private String name;

    @Schema(description = "Product image URL", example = "https://example.com/similac.jpg")
    private String image;

    @Schema(description = "Product price", example = "29.99")
    private double price;

    @Schema(description = "Product description", example = "Milk formula for pregnant women")
    private String description;

    @Schema(description = "Category ID", example = "1")
    private int categoryId;

    @Schema(description = "Product quantity", example = "100")
    private int quantity;

    @Schema(description = "Product status(true = is stock, false = out off stock)", example = "true")
    private boolean status;

    @Schema(description = "Product is delete", example = "false")
    private boolean isDelete;
}