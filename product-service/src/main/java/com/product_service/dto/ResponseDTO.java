package com.product_service.dto;

import java.time.LocalDateTime;

public class ResponseDTO<T> {
    private LocalDateTime localDateTime;
    private int status;
    private String message;
    private T data;


    public ResponseDTO(T data) {
        this.localDateTime = LocalDateTime.now();
        this.status = 200;
        this.data = data;
    }

    public ResponseDTO(int status, T data) {
        this.localDateTime = LocalDateTime.now();
        this.status = status;
        this.data = data;
    }

    public ResponseDTO(String message, T data) {
        this.localDateTime = LocalDateTime.now();
        this.status = 200;
        this.message = message;
        this.data = data;
    }

    public ResponseDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "localDateTime=" + localDateTime +
                ", status=" + status +
                ", data=" + data +
                '}';
    }
}


