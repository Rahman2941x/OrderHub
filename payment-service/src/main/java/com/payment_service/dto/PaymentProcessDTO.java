package com.payment_service.dto;

import java.math.BigDecimal;

public record PaymentProcessDTO(
        Long orderId,
        Long customerId,
        BigDecimal amount,
        String customerEmail
) {
}
