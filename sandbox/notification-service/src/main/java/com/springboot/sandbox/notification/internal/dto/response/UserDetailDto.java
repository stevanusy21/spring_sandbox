package com.springboot.sandbox.notification.internal.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class UserDetailDto {
    private Long id;
    private LocalDateTime createdDate;
    private String username;
    private String fullName;
    private LocalDate dob;
    private String email;
    private String phoneNumber;
    private String address;
    private String status;
}
