package com.email_service.service;

import com.email_service.dto.EmailContent;
import com.email_service.dto.PaymentStatus;
import com.email_service.entity.Email;
import com.email_service.entity.EmailStatus;
import com.email_service.repository.EmailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class EmailSenderService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    EmailRepo EmailRepo;

    public void sendEmail(String toEmail,
                          String subject,
                          String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setText(body);
        mailMessage.setSubject(subject);

        javaMailSender.send(mailMessage);

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        email.setStatus(EmailStatus.SUCCESS);
        email.setCreatedAt(LocalDateTime.now());
        email.setSentAt(LocalDateTime.now());
        EmailRepo.save(email);

        System.out.println("Mail has been successfully send to: " + toEmail);
    }

    public EmailContent buildOrderPlacedEmail(
            String orderCode,
            String orderDate,
            String address
    ) {

        String subject = "Order Placed Successfully";

        String body = """
                Dear Customer,
                
                Thank you for your order.
                
                Your order has been placed successfully and is currently being processed.
                
                Order Code : %s
                Order Date : %s
                Delivery Address : %s
                
                We will notify you once your order is confirmed.
                
                Thank you for shopping with us.
                """
                .formatted(orderCode, orderDate, address);

        return new EmailContent(subject, body);
    }

    public EmailContent buildOrderConfirmedEmail(
            String orderCode,
            String orderDate,
            String address
    ) {

        String subject = "Order Confirmed";

        String body = """
                Dear Customer,
                
                Your order has been confirmed and is being prepared for shipment.
                
                Order Code : %s
                Order Date : %s
                Delivery Address : %s
                
                We will notify you once your order is out for delivery.
                
                Thank you for shopping with us.
                """
                .formatted(orderCode, orderDate, address);

        return new EmailContent(subject, body);
    }

    public EmailContent buildOutForDeliveryEmail(
            String orderCode,
            String address
    ) {

        String subject = "Order Out For Delivery";

        String body = """
                Dear Customer,
                
                Your order is out for delivery and will reach you soon.
                
                Order Code : %s
                Delivery Address : %s
                
                Please keep your phone available for delivery updates.
                
                Thank you for shopping with us.
                """
                .formatted(orderCode, address);

        return new EmailContent(subject, body);
    }

    public EmailContent buildOrderDeliveredEmail(
            String orderCode,
            String deliveryDate,
            String address
    ) {

        String subject = "Order Delivered";

        String body = """
                Dear Customer,
                
                Great news! Your order has been delivered successfully.
                
                Order Code : %s
                Delivered On : %s
                Delivery Address : %s
                
                We hope you enjoy your purchase.
                
                Thank you for shopping with us.
                """
                .formatted(orderCode, deliveryDate, address);

        return new EmailContent(subject, body);
    }

    public EmailContent buildOrderCancelledEmail(
            String orderCode,
            String cancellationDate,
            String address
    ) {

        String subject = "Order Cancelled";

        String body = """
                Dear Customer,
                
                We would like to inform you that your order has been cancelled.
                
                Order Code : %s
                Cancellation Date : %s
                Delivery Address : %s
                
                If you did not request this cancellation or have any questions,
                please contact our support team.
                
                Thank you for shopping with us.
                """
                .formatted(orderCode, cancellationDate, address);

        return new EmailContent(subject, body);
    }

    public EmailContent buildPaymentSuccessEmail(
            BigDecimal amount
    ) {
        String subject = "Payment Successful";

        String body = """
                Dear Customer,
                
                Your payment of ₹%s has been processed successfully.
                
                Transaction Status: SUCCESS
                Amount: ₹%s
                
                Thank you for your purchase.
                
                Regards,
                Support Team
                """.formatted(amount, amount);

        return new EmailContent(subject, body);
    }

    public EmailContent buildPaymentFailureEmail(
            BigDecimal amount
    ) {
        String subject = "Payment Failed";
        String body = """
                Dear Customer,
                
                We regret to inform you that your payment of ₹%s could not be processed.
                
                Transaction Status: FAILED
                Amount: ₹%s
                
                Please try again or contact support if the issue persists.
                
                Regards,
                Support Team
                """.formatted(amount, amount);

        return new EmailContent(subject, body);
    }

}
