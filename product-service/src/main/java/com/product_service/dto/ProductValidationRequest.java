package com.product_service.dto;

public record ProductValidationRequest(
        Long productId,
        Integer quantity
) {
}
