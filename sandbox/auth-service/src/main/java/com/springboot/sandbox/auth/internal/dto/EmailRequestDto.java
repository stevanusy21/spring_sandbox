package com.springboot.sandbox.auth.internal.dto;

import com.springboot.sandbox.common.enumeration.EmailTemplate;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmailRequestDto {
    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private EmailTemplate template;
}
