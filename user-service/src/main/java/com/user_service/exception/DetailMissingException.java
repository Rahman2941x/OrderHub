package com.user_service.exception;

public class DetailMissingException extends RuntimeException {
    public DetailMissingException() {
        super("Expected details is being Null");
    }
}
