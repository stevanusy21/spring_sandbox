package com.springboot.sandbox.auth.external.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class UserResetPasswordDto {
    @NotBlank
    private String password;
}
