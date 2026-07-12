package com.email_service.listener;

import com.email_service.dto.EmailContent;
import com.email_service.dto.EmailRequestDTO;
import com.email_service.dto.PaymentRequestDTO;
import com.email_service.dto.PaymentStatus;
import com.email_service.service.EmailSenderService;
import com.email_service.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmailListener {

    @Autowired
    Utils utils;

    @Autowired
    EmailSenderService emailSenderService;

    @RabbitListener(queuesToDeclare = {@Queue("${oh.email.queue}")})
    public void sendEmailListener(String emailRequest) throws JsonProcessingException {

        System.out.println("Received Message: " + emailRequest);

        EmailRequestDTO requestDTO = (EmailRequestDTO)
                utils.convertJsonToObject(emailRequest, EmailRequestDTO.class);

        if (requestDTO != null) {
            EmailContent emailContent = switch (requestDTO.orderStatus()) {
                case PLACED -> emailSenderService.buildOrderPlacedEmail(
                        requestDTO.orderUuid(),
                        requestDTO.orderDate(),
                        requestDTO.address());

                case CONFIRMED -> emailSenderService.buildOrderConfirmedEmail(
                        requestDTO.orderUuid(),
                        requestDTO.orderDate(),
                        requestDTO.address());

                case OUT_FOR_DELIVERY -> emailSenderService.buildOutForDeliveryEmail(
                        requestDTO.orderUuid(),
                        requestDTO.address());

                case DELIVERED -> emailSenderService.buildOrderDeliveredEmail(
                        requestDTO.orderUuid(),
                        LocalDateTime.now().toString(),
                        requestDTO.address());

                case CANCELLED -> emailSenderService.buildOrderCancelledEmail(
                        requestDTO.orderUuid(),
                        requestDTO.orderDate(),
                        requestDTO.address());
            };
            emailSenderService.sendEmail(
                    requestDTO.toEmail(),
                    emailContent.subject(),
                    emailContent.body()
            );
        }
    }


    @RabbitListener(queuesToDeclare = {@Queue("${oh.email.payment.queue}")})
    public void processPaymentEmail(String paymentRequest) throws JsonProcessingException {

        System.out.println("Received Message: " + paymentRequest);

        PaymentRequestDTO paymentRequestDTO = (PaymentRequestDTO) utils.convertJsonToObject(paymentRequest, PaymentRequestDTO.class);

        System.out.println("Message converted to json" + paymentRequestDTO);


        if (paymentRequestDTO != null) {
            EmailContent emailContent = switch (paymentRequestDTO.paymentStatus()) {
                case SUCCESS -> emailSenderService.buildPaymentSuccessEmail(
                        paymentRequestDTO.Amount()
                );
                case PENDING -> null;
                case FAILURE -> emailSenderService.buildPaymentFailureEmail(
                        paymentRequestDTO.Amount()
                );
            };

            emailSenderService.sendEmail(
                    paymentRequestDTO.toEmail(),
                    emailContent.subject(),
                    emailContent.body()
            );
        }
    }

}

