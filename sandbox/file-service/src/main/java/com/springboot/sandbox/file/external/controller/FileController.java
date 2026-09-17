package com.springboot.sandbox.file.external.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.file.external.dto.request.FileConfirmDto;
import com.springboot.sandbox.file.external.dto.request.PresignedUrlRequestDto;
import com.springboot.sandbox.file.external.dto.response.FileResponseDto;
import com.springboot.sandbox.file.external.dto.response.PresignedUrlResponseDto;
import com.springboot.sandbox.file.external.service.FileService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
@RequestMapping("/api/files")
@RequiredArgsConstructor 
public class FileController {
    private final FileService fileService;

    @PostMapping("/presigned-url")
    public ApiResponse<PresignedUrlResponseDto> createPresignedUrl(@RequestBody PresignedUrlRequestDto request) {
        return ApiResponse.success(fileService.createPresignedUrl(request), "Presigned URL generated successfully");
    }

    @PostMapping("/confirm")
    public ApiResponse<FileResponseDto> confirmUpload(@RequestBody FileConfirmDto request) {
        return ApiResponse.success(fileService.confirmUpload(request), "File uploaded successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<FileResponseDto> getFileDetail(@PathVariable Long id) {
        return ApiResponse.success(fileService.getFileDetail(id), "File details retrieved successfully");
    }

    @GetMapping("/{id}/download")
    public ApiResponse<String> getDownloadUrl(@PathVariable Long id) {
        return ApiResponse.success(fileService.getDownloadUrl(id), "Download URL retrieved successfully");
    }
    
}
