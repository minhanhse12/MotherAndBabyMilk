package com.motherandbabymilk.dto.response;

import lombok.Data;

@Data
public class OrderItemResponse {
    private int id;
    private int productId;
    private int quantity;
    private double price;
}
