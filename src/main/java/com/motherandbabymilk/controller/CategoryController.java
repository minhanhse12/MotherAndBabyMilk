package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.request.CategoryRequest;
import com.motherandbabymilk.dto.response.CategoryResponse;
import com.motherandbabymilk.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Categories")
@SecurityRequirement(name = "api")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request ) {
        CategoryResponse response = categoryService.createCategory(request)    ;
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable("id") int id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategorys() {
        List<CategoryResponse> Categories = categoryService.getAllCategories();
        return ResponseEntity.ok(Categories);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("id") int id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategories(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}