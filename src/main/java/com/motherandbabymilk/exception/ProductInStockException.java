package com.motherandbabymilk.exception;

public class ProductInStockException extends RuntimeException {
    public ProductInStockException(String message) {
        super(message);
    }
}