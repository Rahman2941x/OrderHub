package com.user_service.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime localDateTime,
        int status,
        String error
) {
    public ErrorResponseDTO(HttpStatus httpStatus, String message) {
        this(LocalDateTime.now(),httpStatus.value(),message);
    }
}
