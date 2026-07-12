package com.order_service.filter;

import com.order_service.dto.UserPrincipal;
import com.order_service.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;

        System.out.println("Auth Header: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        System.out.println("Token: " + token);

        if (token != null) {
            Claims claims = jwtUtil.extractClaims(token);

            Long userId = claims.get("userId", Long.class);
            String role = claims.get("Role", String.class);
            String userEmail = claims.getSubject();

            UserPrincipal userPrincipal = new UserPrincipal(userId, userEmail);


            System.out.println("User id: " + userId);
            System.out.println("role: " + role);
            List<GrantedAuthority> authority = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                if (jwtUtil.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authority);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println(SecurityContextHolder.getContext().getAuthentication());
                }
            }
        }
        filterChain.doFilter(request, response);

    }
}
