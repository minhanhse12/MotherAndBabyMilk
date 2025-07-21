package com.motherandbabymilk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Categories category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brands brand;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "status", nullable = false)
    private boolean status = true;

    @Column(name = "is_delete", nullable = false)
    private boolean isDelete = false;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "productId")
    private List<OrderItem> orderItems;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (quantity > 0) {
            this.status = true;
        } else {
            this.status = false;
        }
    }

    @JsonIgnore
    public ProductLine getProductLine() {
        String catName = this.category.getName().toLowerCase();
        if (catName.contains("mẹ")) {
            return ProductLine.MOTHER;
        } else if (catName.contains("bé")) {
            return ProductLine.BABY;
        }
        return null;
    }
}