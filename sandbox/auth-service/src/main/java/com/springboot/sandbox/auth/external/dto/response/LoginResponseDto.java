package com.springboot.sandbox.auth.external.dto.response;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
public class LoginResponseDto {
    private String token;
    private String tokenType;
    private Long expiresIn;
    private String username;
    private String role;
}
