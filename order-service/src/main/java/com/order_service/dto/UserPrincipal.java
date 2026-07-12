package com.order_service.dto;

public record UserPrincipal(
        Long userId,
        String email
) {
}
