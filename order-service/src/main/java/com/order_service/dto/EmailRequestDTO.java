package com.order_service.dto;


import com.order_service.entity.OrderStatus;

public record EmailRequestDTO(
        String toEmail,
        OrderStatus orderStatus,
        String orderUuid,
        String orderDate,
        String address
) {
}
