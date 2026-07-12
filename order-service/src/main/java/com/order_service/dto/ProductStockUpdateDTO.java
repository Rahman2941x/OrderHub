package com.order_service.dto;

import com.order_service.entity.Operation;

public record ProductStockUpdateDTO(
        Long productId,
        Integer stock,
        Operation operation
) {
}
