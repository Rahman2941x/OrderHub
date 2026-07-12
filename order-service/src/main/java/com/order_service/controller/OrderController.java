package com.order_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order_service.dto.*;
import com.order_service.entity.OrderStatus;
import com.order_service.service.OrderService;
import com.order_service.utils.JwtUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    JwtUserUtil jwtUserUtil;

    @Autowired
    OrderService orderService;

    @GetMapping("/check")
    public ResponseEntity<ResponseDTO<String>> getMessage(@RequestParam String message) {
        message = "This is logged user:" + jwtUserUtil.getLoggedUserEmail();
        return ResponseEntity.ok(

                new ResponseDTO<>(message)
        );
    }

    @GetMapping("/check/mail")
    public ResponseEntity<ResponseDTO<String>> sentMail() throws JsonProcessingException {

        orderService.sendEmail(
                OrderStatus.CONFIRMED,
                "ABCDEFGH",
                LocalDateTime.now(),
                "Masuthi Street"
        );

        return ResponseEntity.ok(
                new ResponseDTO<>("Email event sent successfully")
        );
    }

    @PostMapping("/place-order")
    public ResponseEntity<ResponseDTO<?>> placeOrder(@RequestBody OrderRequest request) throws JsonProcessingException {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.placeOrder(request)
                ));
    }

    @PostMapping("/confirm-order")
    public ResponseEntity<ResponseDTO<?>> confirmOrder(@RequestBody OrderConfirmationDTO confirmationDTO) throws JsonProcessingException {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.confirmOrder(confirmationDTO)
                )
        );
    }

    @PostMapping("/cancel-order")
    public ResponseEntity<ResponseDTO<?>> cancelOrder(@RequestBody OrderConfirmationDTO confirmationDTO) throws JsonProcessingException {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.cancelOrder(confirmationDTO)
                )
        );
    }

    @GetMapping("/queue/check")
    public ResponseEntity<ResponseDTO<String>> getMessageFromQueue(@RequestParam String message) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.sendMessageToQueue(message)
                ));
    }

    @GetMapping("/validate/product")
    public ResponseEntity<ResponseDTO<?>> getValidateResponse(@RequestBody ProductValidationRequest request) throws Exception {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.validateProduct(request)
                )
        );
    }

    @GetMapping("/validate/list/product")
    public ResponseEntity<ResponseDTO<?>> validateListOfProduct(@RequestBody List<ProductValidationRequest> requestList) throws JsonProcessingException {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.validateListOfProduct(requestList)
                )
        );
    }

    @PostMapping("/stock/update")
    public ResponseEntity<ResponseDTO<?>> stockUpdate(@RequestBody ProductStockUpdateDTO stockUpdateDTO) throws JsonProcessingException {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.productStockUpdate(stockUpdateDTO)
                )
        );
    }

    @GetMapping("/get/id")
    public ResponseEntity<ResponseDTO<?>> getOrderById(@RequestParam Long orderId) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.getOrderById(orderId)
                )
        );
    }

    @GetMapping("/get/my-orders/customer")
    public ResponseEntity<ResponseDTO<?>> getMyOrder(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.getMyOrders(page, size)
                )
        );
    }

    @GetMapping("/get/all")
    public ResponseEntity<ResponseDTO<?>> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.getAllOrders(page, size)
                )
        );
    }

    @GetMapping("/get/status")
    public ResponseEntity<ResponseDTO<?>> getOrdersByStatus(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam OrderStatus orderStatus) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.getAllOrdersByStatus(page, size, orderStatus)
                )
        );
    }

    @DeleteMapping("/delete/order/id")
    public ResponseEntity<ResponseDTO<?>> deleteOrderById(@RequestParam Long orderId) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.deleteByOrderId(orderId)
                )
        );
    }

    @PostMapping("/product-delivery/id")
    public ResponseEntity<ResponseDTO<?>> productDeliveryComplete(@RequestParam Long orderId) throws JsonProcessingException {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        orderService.deliveryComplete(orderId)
                )
        );
    }

}
