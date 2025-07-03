package com.motherandbabymilk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {
    @Schema(description = "Category name", example = "For Baby 1-6")
    @NotBlank(message = "Name cannot be blank!")
    @Size(max = 100, message = "Name must be less than 100 characters!")
    private String name;

    @Schema(description = "Category description", example = "Use for 1-6 month baby")
    @Size(max = 500, message = "Description must be less than 500 characters!")
    private String description;
}
