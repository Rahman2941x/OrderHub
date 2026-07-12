package com.user_service.exception;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String email) {
        super("User is Not Active "+email);
    }
}
