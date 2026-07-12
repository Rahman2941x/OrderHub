package com.payment_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment_service.dto.PaymentProcessDTO;
import com.payment_service.service.PaymentService;
import com.payment_service.utils.Utils;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class paymentListener {

    @Autowired
    Utils utils;

    @Autowired
    PaymentService paymentService;

    @RabbitListener(queuesToDeclare = {@Queue("${rabbitmq.payment.queue}")})
    public String processPayment(String request) throws JsonProcessingException {

        PaymentProcessDTO paymentRequest = (PaymentProcessDTO) utils.convertJsonToObject(request, PaymentProcessDTO.class);

        return paymentService.updatePayment(paymentRequest);
    }
}
