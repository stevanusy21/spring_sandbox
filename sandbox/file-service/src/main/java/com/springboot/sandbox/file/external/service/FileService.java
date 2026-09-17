package com.springboot.sandbox.file.external.service;

import com.springboot.sandbox.common.dto.PageResponse;
import com.springboot.sandbox.common.enumeration.FileCategory;
import com.springboot.sandbox.common.exception.NotFoundException;
import com.springboot.sandbox.file.entity.Files;
import com.springboot.sandbox.file.external.dto.request.FileConfirmDto;
import com.springboot.sandbox.file.external.dto.request.PresignedUrlRequestDto;
import com.springboot.sandbox.file.external.dto.response.FileResponseDto;
import com.springboot.sandbox.file.external.dto.response.PresignedUrlResponseDto;
import com.springboot.sandbox.file.repository.FilesRepository;
import com.springboot.sandbox.file.util.FilesMapper;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final S3Presigner s3Presigner;
    private final FilesRepository fileRepository;
    private final FilesMapper filesMapper;
    @Value("${supabase.s3.endpoint}")
    private String s3Endpoint;

    public PresignedUrlResponseDto createPresignedUrl(PresignedUrlRequestDto request) {
        String bucketName = request.getFileCategory().getBucketName();
        String fileKey = String.format("%s/%s-%s", 
            request.getFileCategory().name().toLowerCase(),
            UUID.randomUUID(),
            request.getFileName()
        );

        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .contentType(request.getContentType())
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(objectRequest)
            .build();

        PresignedPutObjectRequest presignedPut = s3Presigner.presignPutObject(presignRequest);

        String uploadUrl = presignedPut.url().toString();
        String publicUrl = String.format("%s/object/public/%s/%s", 
            s3Endpoint.replace("/s3", ""),
            bucketName,
            fileKey
        );

        PresignedUrlResponseDto response = new PresignedUrlResponseDto();
        response.setUploadUrl(uploadUrl);
        response.setFileKey(fileKey);
        response.setPublicUrl(publicUrl);

        log.info("Generated presigned upload URL for fileKey: {}", fileKey);
        return response;
    }

    public FileResponseDto confirmUpload(FileConfirmDto request) {
        log.info("Confirmed upload for fileKey: {}", request.getFileKey());

        Files savedFile = fileRepository.save(filesMapper.toFiles(request));
        log.info("Saved file metadata to db with ID: {}", savedFile.getId());

        return filesMapper.toFileResponseDto(savedFile);
    }

    public FileResponseDto getFileDetail(Long id) {
        Files file = fileRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("File not found with ID: " + id));
        
        return filesMapper.toFileResponseDto(file);
    }

    public String getDownloadUrl(Long id) {
        Files file = fileRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("File not found with ID: " + id));
        
        if (file.getFileCategory().isPublic()) {
            return file.getFilePublicUrl();
        } else {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(file.getFileCategory().getBucketName())
                .key(file.getFileKey())
                .responseContentDisposition("attachment;filename=\"" + file.getFileName() + "\"")
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedGet = s3Presigner.presignGetObject(presignRequest);
            
            return presignedGet.url().toString();
        }
    }

    public PageResponse<FileResponseDto> findFilesWithPaging(Long userId, FileCategory fileCategory, int page, int pageSize, String sortBy, Direction sortDirection) {
        Pageable pageable = PageRequest.of(page, pageSize, sortDirection, sortBy);
        Page<Files> files = fileRepository.findAllByUserIdAndFileCategory(userId, fileCategory, pageable);
        return PageResponse.from(files.map(filesMapper::toFileResponseDto));
    }
}
