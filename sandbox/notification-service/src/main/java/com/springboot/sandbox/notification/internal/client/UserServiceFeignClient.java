package com.springboot.sandbox.notification.internal.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.notification.internal.dto.response.UserDetailDto;

@FeignClient(name = "user-service")
public interface UserServiceFeignClient {
    @GetMapping("/api/internal/users/user-detail/{id}")
    ApiResponse<UserDetailDto> findUserDetailInternal(@PathVariable Long id);
}
