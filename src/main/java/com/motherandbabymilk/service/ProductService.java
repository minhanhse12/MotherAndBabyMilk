package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.ProductRequest;
import com.motherandbabymilk.dto.response.ProductResponse;
import com.motherandbabymilk.dto.EmailDetail;
import com.motherandbabymilk.entity.Product;
import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.entity.Roles;
import com.motherandbabymilk.exception.DuplicateProductException;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.repository.ProductRepository;
import com.motherandbabymilk.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ModelMapper modelMapper;

    public ProductResponse createProduct(ProductRequest request) {
        Product existingProduct = productRepository.findByName(request.getName());
        if (existingProduct != null) {
            throw new DuplicateProductException("Product with name " + request.getName() + " already exists");
        }
        Product product = modelMapper.map(request, Product.class);
        product.setId(0);
        product.setDelete(false);
        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductResponse.class);
    }

    public ProductResponse getProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        return modelMapper.map(product, ProductResponse.class);
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAllNotDelete();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }

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

    public void deleteProduct(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        product.setDelete(true);
        productRepository.save(product);
    }
}