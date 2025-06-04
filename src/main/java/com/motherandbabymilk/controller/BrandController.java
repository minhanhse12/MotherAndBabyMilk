package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.request.BrandRequest;
import com.motherandbabymilk.dto.response.BrandResponse;
import com.motherandbabymilk.service.BrandService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@SecurityRequirement(name = "api")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request ) {
        BrandResponse response = brandService.createBrand(request)    ;
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable("id") int id) {
        BrandResponse response = brandService.getBrandById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable("id") int id, @Valid @RequestBody BrandRequest request) {
        BrandResponse response = brandService.updateBrands(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<String> deleteBrand(@PathVariable("id") int id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}