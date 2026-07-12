package com.order_service.serviceTest;

import com.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;


public class OrderServiceTest {

    @Test
    void testUuidGenerate() {

        OrderService orderService = new OrderService();

        String address = "Plot123, Anna Nagar, Madurai, Tamil Nadu";
        Long userId = 1L;
        LocalDateTime time = LocalDateTime.parse("2026-06-06T13:29:06.603570");

        String uuid = orderService.generateOrderUuid(address, userId, time);

        assertEquals("PLOT120260606T1", uuid);
        System.out.println(uuid);

    }

}
