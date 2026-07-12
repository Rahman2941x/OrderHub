package com.order_service.dto;

public record ProductValidationRequest(
        Long productId,
        Integer quantity
) {
}
