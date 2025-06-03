package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.CategoryRequest;
import com.motherandbabymilk.dto.response.CategoryResponse;
import com.motherandbabymilk.entity.Categories;
import com.motherandbabymilk.exception.DuplicateProductException;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.repository.CategoriesRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private ModelMapper modelMapper;


    public CategoryResponse createCategory(@Valid CategoryRequest request) {
        Categories existingCategory = categoriesRepository.findByName(request.getName());
        if (existingCategory!= null) {
            throw new DuplicateProductException("Category with name " + request.getName() + " already exists");
        }
        Categories category = modelMapper.map(request, Categories.class);
        category.setId(0);
        category.setDelete(false);
        Categories savedProduct = categoriesRepository.save(category);

        return modelMapper.map(savedProduct, CategoryResponse.class);
    }

    public CategoryResponse updateCategories(int id, @Valid CategoryRequest request) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categories with ID " + id + " not found"));
        Categories existingCategory = categoriesRepository.findByName(request.getName());
        if (existingCategory != null && existingCategory.getId() != id) {
            throw new DuplicateProductException("Category with name " + request.getName() + " already exists");
        }
        modelMapper.map(request, category);
        category.setId(id);
        Categories updatedCategories = categoriesRepository.save(category);
        return modelMapper.map(updatedCategories, CategoryResponse.class);
    }

    public CategoryResponse getCategoryById(int id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + id + " not found"));
        return modelMapper.map(category, CategoryResponse.class);
    }

    public List<CategoryResponse> getAllCategories() {
        List<Categories> categories = categoriesRepository.findAllNotDelete();
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }
    public void deleteCategory(int id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        category.setDelete(true);
        categoriesRepository.save(category);
    }
}

