package com.order_service.utils;

import com.order_service.dto.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtUserUtil {
    public Long getLoggerUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("user not authenticated");
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.userId();
    }

    public String getLoggedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("user not authenticated");
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.email();
    }
}
