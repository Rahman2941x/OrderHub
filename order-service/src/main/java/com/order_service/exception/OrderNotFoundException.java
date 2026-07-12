package com.order_service.exception;

import com.order_service.dto.OrderConfirmationDTO;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
