package com.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.order_service.constant.CommonConstant;
import com.order_service.dto.*;
import com.order_service.entity.*;
import com.order_service.exception.EmptyDetailException;
import com.order_service.exception.OrderNotFoundException;
import com.order_service.exception.ProductNotAvailableException;
import com.order_service.repository.OrderItemRepository;
import com.order_service.repository.OrderRepository;
import com.order_service.utils.JwtUserUtil;
import com.order_service.utils.MapUtil;
import com.order_service.utils.MqUtil;
import com.order_service.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Value("${rabbitmq.send.message}")
    String sendMessageQueue;

    @Value("${rabbitmq.product.validate.queue}")
    String validateProductQueue;

    @Value("${rabbitmq.bulk.product.validate.queue}")
    String validateBulkProductQueue;

    @Value("${rabbitmq.product-stock.update.queue}")
    String stockUpdateQueue;

    @Value("${rabbitmq.payment.queue}")
    String paymentQueue;

    @Value("${oh.email.queue}")
    String emailQueue;

    @Autowired
    MqUtil mqUtil;

    @Autowired
    Utils utils;
    @Autowired
    JwtUserUtil jwtUserUtil;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    public OrderResponseDTO placeOrder(OrderRequest request) throws JsonProcessingException {

        List<ProductValidationResponse> productValidation = validateListOfProduct
                (request.productQuantityList())
                .stream()
                .filter(ProductValidationResponse::isProductAvailable)
                .toList();

        if (productValidation.isEmpty()) {
            throw new ProductNotAvailableException("No products/stock available");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ProductValidationResponse product : productValidation) {
            ProductValidationRequest req =
                    request.productQuantityList()
                            .stream()
                            .filter(p -> p.productId().equals(product.productId()))
                            .findFirst()
                            .orElseThrow();

            BigDecimal itemTotal =
                    product.price()
                            .multiply(BigDecimal.valueOf(req.quantity()));


            totalAmount = totalAmount.add(itemTotal);

        }
        Long userId = jwtUserUtil.getLoggerUserId();

        String OrderUuid = generateOrderUuid(request.address(), userId, LocalDateTime.now());

        Order order = new Order();
        order.setCustomerId(userId);
        order.setAddress(request.address());
        order.setTotalAmount(totalAmount);
        order.setPaymentMode(request.paymentMode());

        order.setOrderStatus(
                request.paymentMode() == PaymentMode.COD
                        ? OrderStatus.CONFIRMED
                        : OrderStatus.PLACED
        );
        order.setUuid(OrderUuid);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItemList = new ArrayList<>();

        for (ProductValidationResponse prod : productValidation) {

            OrderItem orderItem = new OrderItem();

            ProductValidationRequest req =
                    request.productQuantityList()
                            .stream()
                            .filter(p -> p.productId().equals(prod.productId()))
                            .findFirst()
                            .orElseThrow();

            orderItem.setProductId(prod.productId());
            orderItem.setProductName(prod.productName());
            orderItem.setProductOwnerId(prod.productOwnerId());
            orderItem.setQuantity(req.quantity());
            orderItem.setTotalPrice(
                    prod.price().multiply(BigDecimal.valueOf(req.quantity()))
            );
            orderItem.setOrder(savedOrder);
            orderItemList.add(orderItem);
        }

        orderItemRepository.saveAll(orderItemList);
        savedOrder.setOrderItems(orderItemList);
        sendEmail(order.getOrderStatus(),
                order.getUuid(),
                order.getOrderDate(),
                order.getAddress());

        return MapUtil.MapToProductResponse(savedOrder);
    }

    public String generateOrderUuid(String address, Long userId, LocalDateTime now) {

        String prefix = address.length() >= 4
                ? address.substring(0, 4).toUpperCase()
                : address.toUpperCase();


        String name = now.toString().replaceAll("-", "")
                .replaceAll(" ", "")
                .replaceAll(":", "")
                .replaceAll("\\.", "").substring(0, 10);

        return prefix + userId + name;
    }

    public String sendMessageToQueue(String message) {

        Object response = rabbitTemplate.convertSendAndReceive(sendMessageQueue, message);
        return response != null ? response.toString() : null;
    }

    public ProductValidationResponse validateProduct(ProductValidationRequest request) throws JsonProcessingException {
        if (request == null) {
            throw new EmptyDetailException("Request is empty: " + request);
        }
        ;
        String response = (String) mqUtil.sendAndReceive(validateProductQueue, request);
        System.out.println("response from product: " + response);
        ProductValidationResponse productResponse = (ProductValidationResponse)
                utils.convertJsonToObject(response, ProductValidationResponse.class);
        log.info("response has been received from queue {} is {}", validateProductQueue, productResponse);
        return productResponse;
    }

    public List<ProductValidationResponse> validateListOfProduct(List<ProductValidationRequest> requestList) throws JsonProcessingException {
        if (requestList.isEmpty()) {
            throw new EmptyDetailException("Request is empty: " + requestList);
        }
        String response = (String) mqUtil.sendAndReceive(validateBulkProductQueue, requestList);

        System.out.println("Object Response from product: " + response);

        List<ProductValidationResponse> productResponse = utils.convertJsonToList(
                response,
                new TypeReference<List<ProductValidationResponse>>() {
                }
        );

        log.info("response has been received from Bulk queue {} is {}", validateBulkProductQueue, productResponse);
        return productResponse;
    }

    public String productStockUpdate(ProductStockUpdateDTO stockUpdateDTO) throws JsonProcessingException {
        if (stockUpdateDTO == null) {
            throw new EmptyDetailException("Empty Request : " + stockUpdateDTO);
        }
        System.out.println("stock request to product: " + stockUpdateDTO);
        String response = (String) mqUtil.sendAndReceive(stockUpdateQueue, stockUpdateDTO);
        System.out.println("stock Response from product: " + response);

        if (response.equalsIgnoreCase(CommonConstant.SUCCESS)) {
            return CommonConstant.SUCCESS;
        }
        return CommonConstant.OUT_OF_STOCK;
    }

    @Transactional
    public String confirmOrder(OrderConfirmationDTO confirmationDTO) throws JsonProcessingException {
        Long orderId = confirmationDTO.orderId();
        Order order = orderRepository.findById(confirmationDTO.orderId())
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with Order id: " + orderId)
                );

        if (!order.getOrderStatus().equals(OrderStatus.PLACED) && !order.getOrderStatus().equals(OrderStatus.PAYMENT_FAILED)) {
            return "Order already " + order.getOrderStatus();
        }

        Map<Long, String> stockUpdateStatus = new HashMap<>();

        if (PaymentMode.COD.equals(order.getPaymentMode())) {
            if (!order.getOrderItems().isEmpty()) {
                boolean allSuccess = true;
                for (OrderItem item : order.getOrderItems()) {
                    ProductStockUpdateDTO stockUpdate = new ProductStockUpdateDTO(
                            item.getProductId(),
                            item.getQuantity(),
                            Operation.SUBTRACT
                    );

                    String response = productStockUpdate(stockUpdate);

                    if (response != null && response.equalsIgnoreCase(CommonConstant.SUCCESS)) {
                        stockUpdateStatus.put(item.getProductId(), CommonConstant.SUCCESS);
                    } else {
                        allSuccess = false;
                        stockUpdateStatus.put(item.getProductId(), CommonConstant.FAILURE);
                    }
                }
                System.out.println("stockUpdateStatus" + stockUpdateStatus);
                System.out.println("allSuccess" + allSuccess);

                order.setOrderStatus(
                        allSuccess
                                ? OrderStatus.CONFIRMED
                                : OrderStatus.OUT_OF_STOCK
                );
                orderRepository.save(order);
                System.out.println("Order status" + order.getOrderStatus());
            }
            if (order.getOrderStatus() == OrderStatus.CONFIRMED) {
                sendEmail(order.getOrderStatus(),
                        order.getUuid(),
                        order.getOrderDate(),
                        order.getAddress());
                return "Order has been confirmed";
            } else return stockUpdateStatus.toString();
        } else if (PaymentMode.ONLINE.equals(order.getPaymentMode())) {
            //TODO send and receive from PaymentService
            if (confirmationDTO.amount().compareTo(order.getTotalAmount()) == 0) {
                if (!order.getOrderItems().isEmpty()) {

                    PaymentProcessDTO paymentProcessDTO = new PaymentProcessDTO(
                            confirmationDTO.orderId(),
                            order.getCustomerId(),
                            confirmationDTO.amount(),
                            jwtUserUtil.getLoggedUserEmail()
                    );

                    System.out.println("Email send to check from order" + paymentProcessDTO.customerEmail());
                    ;
                    String response = (String) mqUtil.sendAndReceive(
                            paymentQueue,
                            paymentProcessDTO);

                    if (CommonConstant.SUCCESS.equalsIgnoreCase(response)) {
                        order.setPaymentStatus(PaymentStatus.SUCCESS);

                        boolean allSuccess = true;
                        for (OrderItem item : order.getOrderItems()) {
                            ProductStockUpdateDTO stockUpdate = new ProductStockUpdateDTO(
                                    item.getProductId(),
                                    item.getQuantity(),
                                    Operation.SUBTRACT
                            );
                            String stockResponse = productStockUpdate(stockUpdate);

                            if (stockResponse != null && stockResponse.equalsIgnoreCase(CommonConstant.SUCCESS)) {
                                stockUpdateStatus.put(item.getProductId(), CommonConstant.SUCCESS);
                            } else {
                                allSuccess = false;
                                stockUpdateStatus.put(item.getProductId(), CommonConstant.FAILURE);
                            }
                        }

                        System.out.println("stockUpdateStatus" + stockUpdateStatus);
                        System.out.println("allSuccess" + allSuccess);

                        order.setOrderStatus(
                                allSuccess
                                        ? OrderStatus.CONFIRMED
                                        : OrderStatus.OUT_OF_STOCK
                        );
                        if (!OrderStatus.CONFIRMED.equals(order.getOrderStatus())) {
                            orderRepository.save(order);
                            sendEmail(order.getOrderStatus(),
                                    order.getUuid(),
                                    order.getOrderDate(),
                                    order.getAddress());
                            return "The order has been cancelled due to insufficient stock.";
                        }
                        orderRepository.save(order);
                        sendEmail(order.getOrderStatus(),
                                order.getUuid(),
                                order.getOrderDate(),
                                order.getAddress());
                        return "Order has been confirmed";
                    } else {
                        order.setPaymentStatus(PaymentStatus.FAILURE);
                        order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
                        orderRepository.save(order);
                        return "Payment failed. Stock restored.";
                    }


//                    boolean allSuccess = true;
//                    for (OrderItem item : order.getOrderItems()) {
//                        ProductStockUpdateDTO stockUpdate = new ProductStockUpdateDTO(
//                                item.getProductId(),
//                                item.getQuantity(),
//                                Operation.SUBTRACT
//                        );
//
//                        String response = productStockUpdate(stockUpdate);
//
//                        if (response != null && response.equalsIgnoreCase(CommonConstant.SUCCESS)) {
//                            stockUpdateStatus.put(item.getProductId(), CommonConstant.SUCCESS);
//                        } else {
//                            allSuccess = false;
//                            stockUpdateStatus.put(item.getProductId(), CommonConstant.FAILURE);
//                        }
//                    }
//                    System.out.println("stockUpdateStatus" + stockUpdateStatus);
//                    System.out.println("allSuccess" + allSuccess);
//
//                    order.setOrderStatus(
//                            allSuccess
//                                    ? OrderStatus.CONFIRMED
//                                    : OrderStatus.OUT_OF_STOCK
//                    );
//
//
//                    if (OrderStatus.CONFIRMED.equals(order.getOrderStatus())) {
//                        PaymentProcessDTO paymentProcessDTO = new PaymentProcessDTO(
//                                confirmationDTO.orderId(),
//                                order.getCustomerId(),
//                                confirmationDTO.amount()
//                        );
//
//                        String response = (String) mqUtil.sendAndReceive(
//                                paymentQueue,
//                                paymentProcessDTO);
//
//                        if (CommonConstant.SUCCESS.equalsIgnoreCase(response)) {
//                            order.setPaymentStatus(PaymentStatus.SUCCESS);
//                            order.setOrderStatus(OrderStatus.CONFIRMED);
//                            orderRepository.save(order);
//                            return "Order has been confirmed";
//                        } else {
//                            // Rollback stock
//                            for (OrderItem item : order.getOrderItems()) {
//
//                                ProductStockUpdateDTO rollback = new ProductStockUpdateDTO(
//                                        item.getProductId(),
//                                        item.getQuantity(),
//                                        Operation.ADD
//                                );
//
//                                productStockUpdate(rollback);
//                            }
//
//                            order.setPaymentStatus(PaymentStatus.FAILURE);
//                            order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
//                            orderRepository.save(order);
//
//                            return "Payment failed. Stock restored.";
//                        }
//                    }
                } else return "Failure due to some issue";
            }
            if (order.getOrderStatus() == OrderStatus.CONFIRMED) {
                sendEmail(order.getOrderStatus(),
                        order.getUuid(),
                        order.getOrderDate(),
                        order.getAddress());
                return "Order has been confirmed";
            } else return stockUpdateStatus.toString();

        }
        return "Failure due to some issue";
    }

    @Transactional
    public String cancelOrder(OrderConfirmationDTO confirmationDTO) throws JsonProcessingException {

        Long orderId = confirmationDTO.orderId();
        Order order = orderRepository.findById(confirmationDTO.orderId())
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with Order id: " + orderId)
                );

        if (order.getOrderStatus().equals(OrderStatus.OUT_FOR_DELIVERY) ||
                order.getOrderStatus().equals(OrderStatus.DELIVERED)) {
            return "Order is already " + order.getOrderStatus()
                    + ". It cannot be cancelled at this stage. You can return it once it is delivered.";
        }
        if (order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            return "Order is already " + order.getOrderStatus();
        }
        if (order.getOrderStatus().equals(OrderStatus.PLACED)) {
            return "Order is currently PLACED. It will be automatically cancelled after 24 hours if not processed.";
        }

        Map<Long, String> stockUpdateStatus = new HashMap<>();

        for (OrderItem item : order.getOrderItems()) {
            ProductStockUpdateDTO stockUpdate = new ProductStockUpdateDTO(
                    item.getProductId(),
                    item.getQuantity(),
                    Operation.ADD
            );

            String response = productStockUpdate(stockUpdate);

            stockUpdateStatus.put(
                    item.getProductId(),
                    CommonConstant.SUCCESS.equalsIgnoreCase(response)
                            ? CommonConstant.SUCCESS
                            : CommonConstant.FAILURE
            );
        }
        boolean allSuccess = stockUpdateStatus.values()
                .stream()
                .allMatch(CommonConstant.SUCCESS::equalsIgnoreCase);

        if (allSuccess) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            sendEmail(order.getOrderStatus(),
                    order.getUuid(),
                    order.getOrderDate(),
                    order.getAddress());
            return "Order cancelled successfully. Stock restored: " + stockUpdateStatus;
        }
        return "Stock rollback partially failed. Manual check required.";
    }

    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with Order id: " + orderId)
                );

        return MapUtil.MapToProductResponse(order);
    }

    public Page<OrderResponseDTO> getMyOrders(int page, int size) {
        Long loggerUserId = jwtUserUtil.getLoggerUserId();

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPageDTO = orderRepository.findByCustomerId(loggerUserId, pageable);

        if (orderPageDTO.isEmpty()) {
            log.warn("No order found with customer id: {}", loggerUserId);
            throw new RuntimeException("User not placed any order yet");
        }

        return orderPageDTO.map(MapUtil::MapToProductResponse);
    }

    public Page<OrderResponseDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPageDTO = orderRepository.findAll(pageable);

        if (orderPageDTO.isEmpty()) {
            log.warn("No order found");
            throw new RuntimeException("No order placed yet");
        }

        return orderPageDTO.map(MapUtil::MapToProductResponse);
    }

    public Page<OrderResponseDTO> getAllOrdersByStatus(int page, int size, OrderStatus orderStatus) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPageDTO = orderRepository.findAllByOrderStatus(orderStatus, pageable);

        if (orderPageDTO.isEmpty()) {
            log.warn("No order found with status :: {}", orderStatus);
            throw new RuntimeException("No order with status: " + orderStatus);
        }

        return orderPageDTO.map(MapUtil::MapToProductResponse);
    }


    public String deleteByOrderId(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with Order id: " + orderId)
                );

        if (order.getOrderStatus() == OrderStatus.DELIVERED ||
                order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            return "Order cannot be deleted after shipment started or delivered.";
        }

        orderRepository.delete(order);

        return CommonConstant.SUCCESS;
    }

    public OrderResponseDTO deliveryComplete(Long orderId) throws JsonProcessingException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with Order id: " + orderId)
                );

        if (order.getOrderStatus().equals(OrderStatus.DELIVERED)) {
            throw new RuntimeException("Order already delivered to customer");
        }

        if (PaymentMode.ONLINE.equals(order.getPaymentMode())) {
            if (!PaymentStatus.SUCCESS.equals(order.getPaymentStatus())) {
                throw new RuntimeException("Payment is not completed, kindly pay as cash to delivery person");
            }
        } else {
            PaymentProcessDTO paymentProcessDTO = new PaymentProcessDTO(
                    order.getId(),
                    order.getCustomerId(),
                    order.getTotalAmount(),
                    jwtUserUtil.getLoggedUserEmail()

            );
            String response = (String) mqUtil.sendAndReceive(
                    paymentQueue,
                    paymentProcessDTO);
        }

        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setOrderStatus(OrderStatus.DELIVERED);

        Order savedOrder = orderRepository.save(order);
        sendEmail(order.getOrderStatus(),
                order.getUuid(),
                order.getOrderDate(),
                order.getAddress());

        return MapUtil.MapToProductResponse(savedOrder);
    }

    public void sendEmail(
            OrderStatus orderStatus,
            String orderUuid,
            LocalDateTime orderDate,
            String address) throws JsonProcessingException {

        EmailRequestDTO requestDTO = new EmailRequestDTO(
                jwtUserUtil.getLoggedUserEmail(),
                orderStatus,
                orderUuid,
                orderDate.toString(),
                address
        );

        mqUtil.send(emailQueue, requestDTO);

    }


}
