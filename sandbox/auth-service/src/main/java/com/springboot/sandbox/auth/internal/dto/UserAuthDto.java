package com.springboot.sandbox.auth.internal.dto;

import lombok.Data;

@Data 
public class UserAuthDto {
    private Long id;
    private String username;
    private String password;
    private String role;
    private String email;
}
