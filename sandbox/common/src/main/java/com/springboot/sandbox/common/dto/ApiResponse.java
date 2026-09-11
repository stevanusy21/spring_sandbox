package com.springboot.sandbox.common.dto;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
