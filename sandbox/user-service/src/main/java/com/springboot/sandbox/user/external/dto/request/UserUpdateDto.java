package com.springboot.sandbox.user.external.dto.request;

import java.time.LocalDate;

import com.springboot.sandbox.common.enumeration.AccountRole;
import com.springboot.sandbox.common.enumeration.AccountStatus;
import com.springboot.sandbox.common.validator.annotation.IndonesianPhoneNumber;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data 
public class UserUpdateDto {
    @Enumerated(EnumType.STRING)
    private AccountRole role;
    private String fullName;
    private LocalDate dob;
    @Email(message = "Invalid email format") 
    private String email;
    @IndonesianPhoneNumber(message = "Invalid phone number format") 
    private String phoneNumber;
    private String address;
    private AccountStatus status;
}
