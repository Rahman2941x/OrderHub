package com.order_service.exception;

public class EmptyDetailException extends RuntimeException {
    public EmptyDetailException(String message) {
        super(message);
    }
}
