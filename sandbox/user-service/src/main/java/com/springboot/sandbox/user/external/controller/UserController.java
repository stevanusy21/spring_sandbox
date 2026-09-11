package com.springboot.sandbox.user.external.controller;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.common.dto.PageResponse;
import com.springboot.sandbox.user.external.dto.request.UserCreateDto;
import com.springboot.sandbox.user.external.dto.request.UserUpdateDto;
import com.springboot.sandbox.user.external.dto.response.UserDto;
import com.springboot.sandbox.user.external.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("")
    public ApiResponse<UserDto> createUser(@RequestBody @Valid UserCreateDto userRequestDto) {
        return ApiResponse.success(userService.createUser(userRequestDto), "User created");
    }

    @PatchMapping("")
    public ApiResponse<UserDto> updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        return ApiResponse.success(userService.updateUser(userUpdateDto), "User updated");
    }

    @GetMapping()
    public PageResponse<UserDto> findUserByPaging(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "DESC") Direction sortDirection
    ) {
        return userService.findUsersWithPaging(page, pageSize, sortBy, sortDirection);
    }

    @GetMapping("/{username}")
    public ApiResponse<UserDto> findUserByUsername(@PathVariable String username) {
        return ApiResponse.success(userService.findUserByUsername(username), "User found");
    }

    @DeleteMapping("/{username}")
    public ApiResponse<Boolean> removeUser(@PathVariable String username) {
        return ApiResponse.success(userService.deleteUser(username), "User deleted");
    }
}
