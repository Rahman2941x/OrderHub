package com.product_service.exception;

public class ProductNotActiveException extends RuntimeException {
    public ProductNotActiveException(String message) {
        super("Product is not Active " + message);
    }
}
