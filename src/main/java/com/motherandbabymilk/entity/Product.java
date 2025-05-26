package com.motherandbabymilk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name cannot be blank!")
    @Size(max = 100, message = "Name must be less than 100 characters!")
    @Column(unique = true)
    private String name;

    @NotBlank(message = "Image URL cannot be blank!")
    private String image;

    @Min(value = 0, message = "Price must be non-negative!")
    private double price;

    @Size(max = 500, message = "Description must be less than 500 characters!")
    private String description;

    @Min(value = 1, message = "Category ID must be positive!")
    private int categoryId;

    @Min(value = 0, message = "Quantity must be non-negative!")
    private int quantity;

    private boolean status = true;
}