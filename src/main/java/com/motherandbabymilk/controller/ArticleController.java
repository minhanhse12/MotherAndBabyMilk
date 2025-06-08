package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.request.ArticleRequest;
import com.motherandbabymilk.dto.response.ArticleResponse;
import com.motherandbabymilk.service.ArticleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@SecurityRequirement(name = "api")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody ArticleRequest request ) {
        ArticleResponse response = articleService.createArticle(request)    ;
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable("id") int id) {
        ArticleResponse response = articleService.getArticleById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        List<ArticleResponse> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable("id") int id, @Valid @RequestBody ArticleRequest request) {
        ArticleResponse response = articleService.updateArticles(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<String> deleteArticle(@PathVariable("id") int id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}