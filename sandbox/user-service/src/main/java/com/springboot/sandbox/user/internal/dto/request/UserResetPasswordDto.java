package com.springboot.sandbox.user.internal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class UserResetPasswordDto {
    @NotBlank
    private String password;
}
