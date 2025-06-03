package com.motherandbabymilk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BrandRequest {
    @Schema(description = "Brand name", example = "Vinamilk")
    @NotBlank(message = "Name cannot be blank!")
    @Size(max = 100, message = "Name must be less than 100 characters!")
    private String name;

    @Schema(description = "Brand image URL", example = "https://example.com/similac.jpg")
    @NotBlank(message = "Image URL cannot be blank!")
    private String image;

    @Schema(description = "Brand description", example = "Vinamilk is a ...")
    @Size(max = 500, message = "Description must be less than 500 characters!")
    private String description;

}
