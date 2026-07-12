package com.product_service.dto;

import java.math.BigInteger;

public record ProductValidationResponse(
        Long productId,
        String productName,
        BigInteger price,
        Boolean isProductAvailable,
        Long productOwnerId,
        Integer stock

) {


    public ProductValidationResponse() {
        this(null, null, BigInteger.ZERO, false, 0L, 0);
    }

    public ProductValidationResponse(Long productId) {
        this(productId, null, BigInteger.ZERO, false, 0L, 0);
    }
}
