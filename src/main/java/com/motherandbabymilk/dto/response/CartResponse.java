package com.motherandbabymilk.dto.response;

import lombok.Data;

import java.util.List;


@Data
public class CartResponse {

    private int id;
    private int userId;
    private List<CartItemResponse> cartItems;
    private double totalPrice;
    private String status;
}