package com.user_service.controller;

import com.user_service.dto.AuthDTO;
import com.user_service.dto.ResponseDTO;
import com.user_service.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/jwt/token")
    public ResponseEntity<ResponseDTO<String>> getJwtToken(@RequestBody AuthDTO authDTO){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authDTO.email(),
                    authDTO.password()
            ));
            return authenticationService.getGwtToken(authDTO.email());
        }catch (Exception e){
            throw e;
        }
    }
}
