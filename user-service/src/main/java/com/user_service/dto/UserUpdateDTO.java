package com.user_service.dto;

import com.user_service.entity.Role;

public record UserUpdateDTO(
        String username,
        String email,
        String mobileNumber,
        String address,
        Boolean isActive
) {
}
