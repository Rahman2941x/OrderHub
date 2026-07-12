package com.payment_service.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private BigDecimal totalAmount;
    private String transactionId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentTime;

    public Payment(Long id, Long orderId, BigDecimal totalAmount, String transactionId, PaymentStatus paymentStatus, LocalDateTime paymentTime) {
        this.id = id;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.transactionId = transactionId;
        this.paymentStatus = paymentStatus;
        this.paymentTime = paymentTime;
    }

    public Payment() {
    }

    @PrePersist
    public void preCreate() {
        this.paymentTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", totalAmount=" + totalAmount +
                ", transactionId='" + transactionId + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", paymentTime=" + paymentTime +
                '}';
    }
}
