package com.product_service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record ProductDTO(
        Long id,
        String productName,
        String category,
        String description,
        Long productOwnerId,
        BigInteger price,
        Integer inStock) {
}
