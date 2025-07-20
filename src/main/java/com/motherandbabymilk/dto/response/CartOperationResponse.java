package com.motherandbabymilk.dto.response;

import lombok.Data;

@Data
public class CartOperationResponse {
    private boolean success;
    private String message;
    private CartResponse cart;

    public CartOperationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public CartOperationResponse(boolean success, String message, CartResponse cart) {
        this.success = success;
        this.message = message;
        this.cart = cart;
    }
}