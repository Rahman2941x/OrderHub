package com.email_service.dto;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        String toEmail,
        PaymentStatus paymentStatus,
        BigDecimal Amount
) {
}
