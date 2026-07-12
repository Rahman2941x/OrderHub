package com.product_service.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime localDateTime,
        int status,
        String error
) {
    public ErrorResponseDTO(int status, String error) {
        this(LocalDateTime.now(), status, error);
    }
}
