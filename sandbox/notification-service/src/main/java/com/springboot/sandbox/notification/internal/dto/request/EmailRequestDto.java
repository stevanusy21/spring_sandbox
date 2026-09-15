package com.springboot.sandbox.notification.internal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmailRequestDto {
    @NotNull 
    private Long userId;
    
    private String subject;

    private String body;

    @NotBlank 
    private String template;
}
