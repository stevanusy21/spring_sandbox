package com.springboot.sandbox.notification.internal.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.notification.internal.dto.request.EmailRequestDto;
import com.springboot.sandbox.notification.internal.dto.response.EmailResponseDto;
import com.springboot.sandbox.notification.internal.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notification/internal")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ApiResponse<EmailResponseDto> sendEmail(@RequestBody EmailRequestDto request) {
        return ApiResponse.success(emailService.sendEmail(request), "Email processed");
    }
}
