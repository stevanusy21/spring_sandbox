package com.springboot.sandbox.user.external.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class UserDto {
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
