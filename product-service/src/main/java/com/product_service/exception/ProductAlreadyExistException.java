package com.product_service.exception;

public class ProductAlreadyExistException extends RuntimeException {
    public ProductAlreadyExistException(String message) {

        super("Product already exist with product name: " + message);
    }
}
