package com.order_service.scheduler;

import com.order_service.entity.Order;
import com.order_service.entity.OrderStatus;
import com.order_service.repository.OrderRepository;
import jdk.dynalink.linker.LinkerServices;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderScheduler {

    private static final Logger log = LoggerFactory.getLogger(OrderScheduler.class);
    @Autowired
    OrderRepository orderRepository;

    @Scheduled(cron = "0 0 * * * ?")
    public void autoCancelOrders() {
        List<Order> orderList = orderRepository.findAllByOrderStatusIn(
                List.of(OrderStatus.PLACED,
                        OrderStatus.OUT_OF_STOCK,
                        OrderStatus.PAYMENT_FAILED));

        for (Order order : orderList) {
            log.info("Order Id={}, Order Status={}, OrderDate={}",
                    order.getId(),
                    order.getOrderStatus(),
                    order.getOrderDate());

            if (order.getOrderDate().isBefore(LocalDateTime.now().minusHours(24))) {
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                log.info("Auto-cancelled order: {}", order.getId());
            }
        }
    }
}
