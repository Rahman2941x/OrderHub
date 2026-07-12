package com.user_service.dto;

import java.time.LocalDateTime;

public class ResponseDTO<T> {
     private LocalDateTime localDateTime;
     private int status;
     private T data;

    public ResponseDTO(int status, T data) {
        this.localDateTime=LocalDateTime.now();
        this.status = status;
        this.data = data;
    }

    public ResponseDTO() {
    }

    public ResponseDTO(T data) {
        this.localDateTime=LocalDateTime.now();
        this.status=200;
        this.data = data;
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

}

