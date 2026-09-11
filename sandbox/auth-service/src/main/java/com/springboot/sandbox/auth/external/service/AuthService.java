package com.springboot.sandbox.auth.external.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.sandbox.auth.external.dto.request.LoginRequestDto;
import com.springboot.sandbox.auth.external.dto.response.LoginResponseDto;
import com.springboot.sandbox.auth.internal.client.UserServiceClient;
import com.springboot.sandbox.auth.internal.dto.UserAuthDto;
import com.springboot.sandbox.common.exception.BadRequestException;
import com.springboot.sandbox.common.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor 
@Slf4j 
public class AuthService {
    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("Attempting login for user: {}", loginRequestDto.getUsername());

        UserAuthDto user = userServiceClient.findUserForAuth(loginRequestDto.getUsername());

        if(user == null){
            log.warn("User not found: {}", loginRequestDto.getUsername());
            throw new BadRequestException("Username or password is incorrect!");
        }

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            log.warn("Password mismatch for user: {}", loginRequestDto.getUsername());
            throw new BadRequestException("Username or password is incorrect!");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());
        
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        log.info("User logged in successfully: {}", loginRequestDto.getUsername());

        return LoginResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
