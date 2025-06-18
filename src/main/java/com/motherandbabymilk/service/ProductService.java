package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.ProductRequest;
import com.motherandbabymilk.dto.response.ProductResponse;
import com.motherandbabymilk.entity.Brands;
import com.motherandbabymilk.entity.Categories;
import com.motherandbabymilk.entity.Product;
import com.motherandbabymilk.exception.DuplicateProductException;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.repository.BrandsRepository;
import com.motherandbabymilk.repository.CategoriesRepository;
import com.motherandbabymilk.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoriesRepository categoryRepository;
    @Autowired
    private BrandsRepository brandRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product existingProduct = productRepository.findByName(request.getName());
        if (existingProduct != null) {
            throw new DuplicateProductException("Product with name " + request.getName() + " already exists");
        }
        Product product = modelMapper.map(request, Product.class);
        Brands brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand not found"));
        Categories category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        product.setBrand(brand);
        product.setCategory(category);
        product.setId(0);
        product.setDelete(false);
        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductResponse.class);
    }
    @Transactional
    public ProductResponse getProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        return modelMapper.map(product, ProductResponse.class);
    }

    @Transactional
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAllNotDelete();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProduct(int id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        Product existingProduct = productRepository.findByName(request.getName());
        if (existingProduct != null && existingProduct.getId() != id) {
            throw new DuplicateProductException("Product with name " + request.getName() + " already exists");
        }
        modelMapper.map(request, product);
        product.setId(id);
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductResponse.class);
    }

    @Transactional
    public void deleteProduct(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        product.setDelete(true);
        productRepository.save(product);
    }
}