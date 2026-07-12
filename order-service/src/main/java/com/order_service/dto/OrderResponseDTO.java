package com.order_service.dto;

import com.order_service.entity.OrderStatus;
import com.order_service.entity.PaymentMode;
import com.order_service.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long orderId,
        Long userId,
        String address,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        PaymentMode paymentMode,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        String orderUuid,
        LocalDateTime orderDate
) {
    public OrderResponseDTO(Long orderId,
                            Long userId,
                            String address,
                            List<OrderItemResponse> items,
                            BigDecimal totalAmount,
                            PaymentMode paymentMode,
                            OrderStatus orderStatus,
                            PaymentStatus paymentStatus,
                            String orderUuid) {
        this(orderId, userId, address, items, totalAmount, paymentMode, orderStatus, paymentStatus, orderUuid, LocalDateTime.now());
    }
}
