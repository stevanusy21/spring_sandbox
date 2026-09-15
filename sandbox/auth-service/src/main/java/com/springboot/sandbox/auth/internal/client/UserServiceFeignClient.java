package com.springboot.sandbox.auth.internal.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.springboot.sandbox.auth.internal.dto.UserAuthDto;
import com.springboot.sandbox.auth.internal.dto.UserCreateDto;
import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.auth.internal.dto.UserDto;

import jakarta.validation.Valid;

@FeignClient(name = "user-service")
public interface UserServiceFeignClient {
    @GetMapping("/api/internal/users/user-auth/{username}")
    ApiResponse<UserAuthDto> findUserForAuth(@PathVariable String username);

    @PostMapping("/api/users")
    ApiResponse<UserDto> createUser(@RequestBody @Valid UserCreateDto userRequestDto);
}
