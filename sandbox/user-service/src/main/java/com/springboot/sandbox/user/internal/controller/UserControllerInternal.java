package com.springboot.sandbox.user.internal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.user.internal.dto.response.UserAuthDto;
import com.springboot.sandbox.user.internal.service.UserServiceInternal;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor 
public class UserControllerInternal {
    private final UserServiceInternal userServiceInternal;
    
    @GetMapping("/{username}")
    public ApiResponse<UserAuthDto> findUserForAuth(@PathVariable String username){
        return ApiResponse.success(userServiceInternal.findUserAuthByUsername(username), "User found");
    }
}
