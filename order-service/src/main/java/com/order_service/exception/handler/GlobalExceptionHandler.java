package com.order_service.exception.handler;

import com.order_service.dto.ErrorResponseDTO;
import com.order_service.exception.EmptyDetailException;
import com.order_service.exception.ProductNotAvailableException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(
                        ex.toString()
                ));
    }

    @ExceptionHandler(EmptyDetailException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmptyException(EmptyDetailException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(
                        ex.toString()
                ));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleJwtExpirationException(ExpiredJwtException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(
                        ex.toString()
                ));
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleJwtExpirationException(ProductNotAvailableException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(
                        ex.toString()
                ));
    }


}
