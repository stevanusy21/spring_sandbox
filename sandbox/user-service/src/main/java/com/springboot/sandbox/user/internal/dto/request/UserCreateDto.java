package com.springboot.sandbox.user.internal.dto.request;

import java.time.LocalDate;

import com.springboot.sandbox.common.enumeration.AccountRole;
import com.springboot.sandbox.common.validator.annotation.IndonesianPhoneNumber;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class UserCreateDto {
    @NotBlank 
    private String username;
    
    @NotBlank 
    private String password;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountRole role;
    
    @NotBlank 
    private String fullName;
    
    @NotNull 
    private LocalDate dob;
    
    @NotBlank 
    @Email(message = "Invalid email format") 
    private String email;
    
    @NotBlank 
    @IndonesianPhoneNumber(message = "Invalid phone number format") 
    private String phoneNumber;

    private String address;
}
