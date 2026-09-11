package com.springboot.sandbox.auth.external.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.sandbox.auth.external.dto.request.LoginRequestDto;
import com.springboot.sandbox.auth.external.dto.request.RegisterDto;
import com.springboot.sandbox.auth.external.dto.response.LoginResponseDto;
import com.springboot.sandbox.auth.internal.client.UserServiceFeignClient;
import com.springboot.sandbox.auth.internal.dto.UserAuthDto;
import com.springboot.sandbox.auth.internal.dto.UserCreateDto;
import com.springboot.sandbox.auth.internal.dto.UserDto;
import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.common.enumeration.AccountRole;
import com.springboot.sandbox.common.exception.BadRequestException;
import com.springboot.sandbox.common.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor 
@Slf4j 
public class AuthService {
    private final UserServiceFeignClient userServiceFeignClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("Attempting login for user: {}", loginRequestDto.getUsername());

        ApiResponse<UserAuthDto> response = userServiceFeignClient.findUserForAuth(loginRequestDto.getUsername());
        UserAuthDto user = response.getData();

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

    public String register(RegisterDto registerDto){
        log.info("Attempting to register user: {}", registerDto.getUsername());
        
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(registerDto.getUsername());
        userCreateDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userCreateDto.setRole(AccountRole.USER);
        userCreateDto.setFullName(registerDto.getFullName());
        userCreateDto.setDob(registerDto.getDob());
        userCreateDto.setEmail(registerDto.getEmail());
        userCreateDto.setPhoneNumber(registerDto.getPhoneNumber());
        userCreateDto.setAddress(registerDto.getAddress());
        
        try{

        }catch(Exception e){
            
        }
        ApiResponse<UserDto> response = userServiceFeignClient.createUser(userCreateDto);
        return response.getMessage();
    }
}
