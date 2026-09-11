package com.springboot.sandbox.auth.external.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.sandbox.auth.external.dto.request.LoginRequestDto;
import com.springboot.sandbox.auth.external.dto.request.RegisterDto;
import com.springboot.sandbox.auth.external.dto.response.LoginResponseDto;
import com.springboot.sandbox.auth.external.service.AuthService;
import com.springboot.sandbox.common.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ApiResponse.success(authService.login(loginRequestDto), "User logged in successfully");
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterDto registerDto) {
        return ApiResponse.success(authService.register(registerDto), "User registered successfully");
    }
    
}
