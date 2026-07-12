package com.user_service.service;

import com.user_service.dto.ResponseDTO;
import com.user_service.entity.User;
import com.user_service.repository.UserRepository;
import com.user_service.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    public ResponseEntity<ResponseDTO<String>> getGwtToken(String email) {

        User user=userRepository.findByEmail(email)
                .orElseThrow(()->
                        new RuntimeException("Email not found"));

        if(!user.getActive()){
            throw  new RuntimeException("User is not active");
        }

        return ResponseEntity.ok(new ResponseDTO<>(200,jwtUtil.generateToken(user)));
    }
}
