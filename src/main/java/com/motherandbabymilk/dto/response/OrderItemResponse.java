package com.motherandbabymilk.dto.response;

import lombok.Data;

@Data
public class OrderItemResponse {
    private int id;
    private int productId;
    private String productName;
    private int quantity;
    private Double UnitPrice;
    private Double totalPrice;
    private String image;
}
