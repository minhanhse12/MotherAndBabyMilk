package com.motherandbabymilk.dto.response;

import lombok.Data;

/**
 * DTO for returning cart item details.
 */
@Data
public class CartItemResponse {

    private int id;
    private int productId;
    private String productName;
    private int quantity;
    private Double UnitPrice;
    private Double totalPrice;
    private String note;
    private boolean isSelected ;
}