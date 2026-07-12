package com.user_service.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {

        super("User Already exist with this email: "+message);
    }
}
