package com.payment_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment_service.dto.PaymentProcessDTO;
import com.payment_service.dto.PaymentRequestDTO;
import com.payment_service.entity.Payment;
import com.payment_service.entity.PaymentStatus;
import com.payment_service.repository.PaymentRepository;
import com.payment_service.utils.MqUtil;
import com.payment_service.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    @Value("${oh.email.payment.queue}")
    String paymentEmail;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    Utils utils;

    @Autowired
    MqUtil mqUtil;

    public String updatePayment(PaymentProcessDTO paymentProcessDTO) throws JsonProcessingException {

        String transactionId = generateTransactionId(paymentProcessDTO);
        Payment payment = new Payment();
        payment.setOrderId(paymentProcessDTO.orderId());
        payment.setTransactionId(transactionId);
        payment.setTotalAmount(paymentProcessDTO.amount());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        log.info("payment has been successfully processed");

        paymentRepository.save(payment);

        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(
                paymentProcessDTO.customerEmail(),
                payment.getPaymentStatus(),
                payment.getTotalAmount()
        );

        mqUtil.send(paymentEmail, paymentRequestDTO);

        return "SUCCESS";
    }

    private String generateTransactionId(PaymentProcessDTO paymentProcessDTO) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "OHP" + timestamp + paymentProcessDTO.orderId();
    }
}
