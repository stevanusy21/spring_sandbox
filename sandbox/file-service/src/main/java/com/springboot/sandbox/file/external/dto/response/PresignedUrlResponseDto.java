package com.springboot.sandbox.file.external.dto.response;

import lombok.Data;

@Data 
public class PresignedUrlResponseDto {
    private String uploadUrl;
    private String fileKey;
    private String publicUrl;
}
