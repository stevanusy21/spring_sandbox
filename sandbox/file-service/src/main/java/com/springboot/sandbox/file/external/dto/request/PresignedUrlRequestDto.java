package com.springboot.sandbox.file.external.dto.request;

import com.springboot.sandbox.common.enumeration.FileCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PresignedUrlRequestDto {
    @NotNull
    private FileCategory fileCategory;
    @NotBlank
    private String fileName;
    @NotBlank
    private String contentType;
}
