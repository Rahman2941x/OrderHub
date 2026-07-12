package com.order_service.utils;


import com.order_service.dto.OrderItemResponse;
import com.order_service.dto.OrderResponseDTO;
import com.order_service.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class MapUtil {

    public static OrderResponseDTO MapToProductResponse(Order order) {

        List<OrderItemResponse> items = getOrderItemResponses(order);

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                order.getAddress(),
                items,
                order.getTotalAmount(),
                order.getPaymentMode(),
                order.getOrderStatus(),
                order.getPaymentStatus(),
                order.getUuid(),
                order.getOrderDate()
        );
    }

    public static List<OrderItemResponse> getOrderItemResponses(Order order) {
        return order.getOrderItems()
                .stream()
                .map(
                        item -> new OrderItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getTotalPrice()
                        )
                ).toList();
    }
}
