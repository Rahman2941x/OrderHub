package com.order_service.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record ProductValidationResponse(
        Long productId,
        String productName,
        BigDecimal price,
        Boolean isProductAvailable,
        Long productOwnerId,
        Integer stock

) {

    public ProductValidationResponse() {
        this(null, null, BigDecimal.ZERO, false, 0L, 0);
    }

    public ProductValidationResponse(Long productId) {
        this(productId, null, BigDecimal.ZERO, false, 0L, 0);
    }
}
