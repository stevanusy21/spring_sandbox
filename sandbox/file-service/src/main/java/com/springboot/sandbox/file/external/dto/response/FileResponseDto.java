package com.springboot.sandbox.file.external.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.sandbox.common.enumeration.FileCategory;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileResponseDto {
    private Long id;
    private String filePublicUrl;
    private String fileName;
    private FileCategory fileCategory;
    private Long fileSize;
    private String fileDescription;
    private String contentType;

    public String getFilePublicUrl() {
        return fileCategory != null && fileCategory.isPublic()
                ? filePublicUrl
                : null;
    }
}
