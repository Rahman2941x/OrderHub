package com.order_service.dto;

import java.math.BigDecimal;

public record OrderConfirmationDTO(
        Long orderId,
        BigDecimal amount
) {
}
