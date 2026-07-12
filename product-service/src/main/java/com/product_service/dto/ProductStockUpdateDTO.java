package com.product_service.dto;

import com.product_service.entity.Operation;

public record ProductStockUpdateDTO(
        Long productId,
        Integer stock,
        Operation operation
) {
}
