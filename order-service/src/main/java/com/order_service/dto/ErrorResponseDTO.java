package com.order_service.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime localDateTime,
        int status,
        String error
) {

    public ErrorResponseDTO(String error) {
        this(LocalDateTime.now(), 403, error);
    }
}
