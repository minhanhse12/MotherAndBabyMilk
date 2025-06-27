package com.motherandbabymilk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleResponse {

    @Schema(description = "Article ID", example = "1")
    private int id;

    @Schema(description = "Title of the article", example = "Benefits of Breastfeeding")
    private String title;

    @Schema(description = "Content of the article", example = "Breastfeeding provides all the nutrition your baby needs...")
    private String content;

    @Schema(description = "URL of the article's image", example = "https://example.com/article-image.jpg")
    private String image;

    @Schema(description = "URL of the article", example = "https://example.com/article.jpg")
    private String link;

    @Schema(description = "Author's name", example = "Dr. Jane Smith")
    private String authorName;

    @Schema(description = "Date the article was created", example = "2025-06-06T10:15:30")
    private LocalDateTime createdDate;

    @Schema(description = "Is delete")
    private boolean isDelete;
}
