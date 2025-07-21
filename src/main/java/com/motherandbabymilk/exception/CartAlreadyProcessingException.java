package com.motherandbabymilk.exception;

public class CartAlreadyProcessingException extends RuntimeException {
    public CartAlreadyProcessingException(String message) {
        super(message);
    }
}
