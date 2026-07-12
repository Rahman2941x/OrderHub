package com.email_service.dto;

import com.email_service.entity.OrderStatus;

public record EmailRequestDTO(
        String toEmail,
        OrderStatus orderStatus,
        String orderUuid,
        String orderDate,
        String address
) {
}
