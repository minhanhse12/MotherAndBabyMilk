package com.motherandbabymilk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleRequest {

    @Schema(description = "Title of the article", example = "Benefits of Breastfeeding")
    @NotBlank(message = "Title cannot be blank!")
    @Size(max = 200, message = "Title must be less than 200 characters!")
    private String title;

    @Schema(description = "Content of the article", example = "Breastfeeding provides all the nutrition your baby needs...")
    @NotBlank(message = "Content cannot be blank!")
    private String content;

    @Schema(description = "URL of the article's image", example = "https://example.com/article-image.jpg")
    @NotBlank(message = "Image URL cannot be blank!")
    private String image;

    @Schema(description = "Author name", example = "Dr. Jane Smith")
    @NotBlank(message = "Author name cannot be blank!")
    @Size(max = 100, message = "Author name must be less than 100 characters!")
    private String authorName;
}
