package com.product_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.product_service.dto.*;
import com.product_service.entity.Product;
import com.product_service.exception.ProductNotActiveException;
import com.product_service.exception.ProductNotFoundException;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductService;
import com.product_service.util.MapToDTO;
import com.product_service.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class ProductListener {

    private final static Logger logger = LoggerFactory.getLogger(ProductListener.class);

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Autowired
    Utils utils;

    @RabbitListener(queuesToDeclare = {@Queue("${rabbitmq.send.message}")})
    public String receiveMessageFrmOrder(String message) {
        System.out.println("Message from order: " + message);

        return "Hello Order this is from Product";
    }

    @RabbitListener(queuesToDeclare = {@Queue("${rabbitmq.product-stock.update.queue}")})
    public String processStockUpdate(String request) throws JsonProcessingException {

        ProductStockUpdateDTO stockUpdateDTO = (ProductStockUpdateDTO)
                utils.convertJsonToObject(request, ProductStockUpdateDTO.class);

        logger.info("Request from order {}", stockUpdateDTO);

        try {
            if (stockUpdateDTO == null) {
                logger.warn("No Data to update the stock");
                return "FAILURE";
            } else {
                boolean isExist = productRepository.existsById(stockUpdateDTO.productId());

                logger.info("product isExist: " + isExist);
                if (!isExist) {
                    logger.error("Product ID {} Not Found", stockUpdateDTO.productId());
                    return "FAILURE";
                }
                logger.info("Request from queue{} is {}", "rabbitmq.product-stock.update.queue", stockUpdateDTO);
                String response = productService.adjustStockById(stockUpdateDTO.productId(),
                        stockUpdateDTO.stock(),
                        stockUpdateDTO.operation());

                logger.info("stock updated status for Product ID: {} is {}", stockUpdateDTO.productId(), response);
                return "SUCCESS";
            }
        } catch (Exception e) {
            logger.error("Error processing stock update for Product ID: {}",
                    stockUpdateDTO.productId(),
                    e);
        }
        return "FAILURE";
    }

    @RabbitListener(queuesToDeclare = {@Queue("${rabbitmq.product.request.queue}")})
    public void processProductDetails(String productid) {

        try {
            Product product = productService.fetchProductById(Long.parseLong(productid));

            ProductDetailResponseDTO response = MapToDTO.mapToProductDetailResponse(product);

            //rabbitTemplate.convertAndSend("${rabbitmq.product.response.queue}", response);

        } catch (ProductNotFoundException ex) {
            logger.error("Product Not Found with id: {}", productid, ex);
            ProductDetailResponseDTO response = new ProductDetailResponseDTO("Product Not Found with id: " + productid);
            //rabbitTemplate.convertAndSend("${rabbitmq.product.response.queue}", response);
        } catch (ProductNotActiveException ex) {
            logger.error("Product is Not Active with id: {}", productid, ex);
            ProductDetailResponseDTO response = new ProductDetailResponseDTO("Product is Not Active with id: " + productid);
            // rabbitTemplate.convertAndSend("${rabbitmq.product.response.queue}", response);

        } catch (Exception e) {
            logger.error("Error processing stock update for Product ID: {}", productid, e);
            ProductDetailResponseDTO response =
                    new ProductDetailResponseDTO("Unexpected Error");
//            rabbitTemplate.convertAndSend("${rabbitmq.product.response.queue}", response);
        }
    }

    @RabbitListener(queuesToDeclare = {@Queue("${rabbitmq.product.validate.queue}")})
    public String validateProduct(String request1) throws JsonProcessingException {

        ProductValidationRequest request = (ProductValidationRequest) utils.convertJsonToObject(request1, ProductValidationRequest.class);
        logger.info("Request from order:: {}", request);
        try {
            if (request == null) {
                logger.error("Request is empty");
                return utils.convertObjectToJson(new ProductValidationResponse());
            }

            String response = utils.convertObjectToJson((ProductValidationResponse)
                    productService.validateProduct(
                            request.productId(),
                            request.quantity()));
            logger.info("Response from product: " + response);

            return response;

        } catch (Exception e) {
            logger.error("Error processing product validation for Product ID: {}",
                    request.productId(),
                    e);
        }
        return utils.convertObjectToJson(new ProductValidationResponse());

    }


    @RabbitListener(queuesToDeclare = @Queue("${rabbitmq.bulk.product.validate.queue}"))
    public String validateListProduct(String requests) throws JsonProcessingException {
        try {
            if (requests == null) {
                logger.error("request is being Null {}", (Object) null);
                return utils.convertObjectToJson(new ProductValidationResponse());
            }

            logger.info("Request list from order:: {}", requests);


            List<ProductValidationRequest> requestList = utils.convertJsonToList(
                    requests,
                    new TypeReference<List<ProductValidationRequest>>() {
                    }
            );

            String response = utils.convertObjectToJson((List<ProductValidationResponse>)
                    productService.validateListProduct(requestList));

            logger.info("Response list from product:: {}", response);

            return response;
        } catch (Exception e) {
            logger.error("Error processing product validation for Product ID: {}",
                    requests,
                    e);
        }

        return utils.convertObjectToJson(new ProductValidationResponse());
    }

}
