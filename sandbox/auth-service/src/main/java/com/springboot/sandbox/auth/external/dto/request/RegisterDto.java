package com.springboot.sandbox.auth.external.dto.request;

import java.time.LocalDate;

import com.springboot.sandbox.common.validator.annotation.IndonesianPhoneNumber;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class RegisterDto {
    @NotBlank 
    private String username;
    
    @NotBlank 
    private String password;
    
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
