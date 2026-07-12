package com.user_service.dto;

import com.user_service.entity.Role;

public record UserDTO(
        String username,
        String password,
        String email,
        String mobileNumber,
        String address,
        Role role,
        Boolean isActive
) {
}
