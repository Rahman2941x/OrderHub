package com.payment_service.dto;

import com.payment_service.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        String toEmail,
        PaymentStatus paymentStatus,
        BigDecimal Amount

) {
}
