package com.springboot.sandbox.file.external.dto.request;

import com.springboot.sandbox.common.enumeration.FileCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class FileConfirmDto {
    private Long userId;
    @NotBlank 
    private String fileKey;
    @NotBlank
    private String filePublicUrl;
    @NotBlank
    private String fileName;
    @NotNull
    private FileCategory fileCategory;
    @NotBlank
    private String contentType;
    private String fileDescription;
}
