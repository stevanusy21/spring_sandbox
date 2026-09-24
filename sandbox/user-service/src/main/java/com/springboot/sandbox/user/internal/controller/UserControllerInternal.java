package com.springboot.sandbox.user.internal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.user.internal.dto.request.UserCreateDto;
import com.springboot.sandbox.user.internal.dto.request.UserResetPasswordDto;
import com.springboot.sandbox.user.internal.dto.response.UserAuthDto;
import com.springboot.sandbox.user.internal.dto.response.UserDetailDto;
import com.springboot.sandbox.user.internal.dto.response.UserEmailDto;
import com.springboot.sandbox.user.internal.service.UserServiceInternal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor 
public class UserControllerInternal {
    private final UserServiceInternal userServiceInternal;

    @PostMapping("")
    public ApiResponse<UserEmailDto> createUserInternal(@RequestBody @Valid UserCreateDto userRequestDto) {
        return ApiResponse.success(userServiceInternal.createUser(userRequestDto), "User created");
    }

    @PatchMapping("/{username}/reset-password")
    public ApiResponse<UserEmailDto> resetPasswordInternal(@PathVariable String username, @RequestBody @Valid UserResetPasswordDto userResetPasswordDto) {
        return ApiResponse.success(userServiceInternal.resetPassword(username, userResetPasswordDto), "Password reset success");
    }
    
    @GetMapping("/user-auth/{username}")
    public ApiResponse<UserAuthDto> findUserAuthInternal(@PathVariable String username){
        return ApiResponse.success(userServiceInternal.findUserAuthByUsername(username), "User found");
    }

    @GetMapping("/user-detail/{id}")
    public ApiResponse<UserDetailDto> findUserDetailInternal(@PathVariable Long id){
        return ApiResponse.success(userServiceInternal.findUserDetailById(id), "User found");
    }
}
