package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.BrandRequest;
import com.motherandbabymilk.dto.response.BrandResponse;
import com.motherandbabymilk.entity.Brands;
import com.motherandbabymilk.exception.DuplicateProductException;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.repository.BrandsRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService{

    @Autowired
    private BrandsRepository brandsRepository;
    @Autowired
    private ModelMapper modelMapper;


    public BrandResponse createBrand(@Valid BrandRequest request) {
        Brands existingBrand = brandsRepository.findByName(request.getName());
        if (existingBrand!= null) {
            throw new DuplicateProductException("Brand with name " + request.getName() + " already exists");
        }
        Brands brand = modelMapper.map(request, Brands.class);
        brand.setId(0);
        brand.setDelete(false);
        Brands savedProduct = brandsRepository.save(brand);

        return modelMapper.map(savedProduct, BrandResponse.class);
    }

    public BrandResponse updateBrands(int id, @Valid BrandRequest request) {
        Brands brand = brandsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brands with ID " + id + " not found"));
        Brands existingBrand = brandsRepository.findByName(request.getName());
        if (existingBrand != null && existingBrand.getId() != id) {
            throw new DuplicateProductException("Brand with name " + request.getName() + " already exists");
        }
        modelMapper.map(request, brand);
        brand.setId(id);
        Brands updatedBrands = brandsRepository.save(brand);
        return modelMapper.map(updatedBrands, BrandResponse.class);
    }

    public BrandResponse getBrandById(int id) {
        Brands brand = brandsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand with ID " + id + " not found"));
        return modelMapper.map(brand, BrandResponse.class);
    }

    public List<BrandResponse> getAllBrands() {
        List<Brands> brands = brandsRepository.findAllNotDelete();
        return brands.stream()
                .map(brand -> modelMapper.map(brand, BrandResponse.class))
                .collect(Collectors.toList());
    }
    public void deleteBrand(int id) {
        Brands brand = brandsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        brand.setDelete(true);
        brandsRepository.save(brand);
    }
}

