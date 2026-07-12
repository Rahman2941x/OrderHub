package com.order_service.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseDTO<T> {

    private LocalDateTime localDateTime;
    private T data;
    private int status;
    private String message;

    public ResponseDTO(int status, T data, String message) {
        this.localDateTime = LocalDateTime.now();
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public ResponseDTO(T data) {
        this.localDateTime = LocalDateTime.now();
        this.status = 200;
        this.message = "Request processed successfully";
        this.data = data;
    }

    public ResponseDTO(T data, String message) {
        this.localDateTime = LocalDateTime.now();
        this.status = 200;
        this.message = message;
        this.data = data;
    }

    public ResponseDTO(LocalDateTime localDateTime, T data, int status, String message) {
        this.localDateTime = localDateTime;
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public ResponseDTO() {
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "localDateTime=" + localDateTime +
                ", data=" + data +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
