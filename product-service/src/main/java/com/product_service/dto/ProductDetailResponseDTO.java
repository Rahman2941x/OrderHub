package com.product_service.dto;

import java.math.BigInteger;

public record ProductDetailResponseDTO(
        Long productId,
        String productName,
        String productCategory,
        String description,
        Long productOwnerId,
        BigInteger price,
        Boolean active,
        Integer inStock,
        String message
) {
    public ProductDetailResponseDTO(String message) {
        this(0L, null, null, null, null, null, null, null, message);
    }
}
