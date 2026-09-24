package com.springboot.sandbox.file.util;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.springboot.sandbox.file.entity.FileEntity;
import com.springboot.sandbox.file.external.dto.request.FileConfirmDto;
import com.springboot.sandbox.file.external.dto.response.FileResponseDto;


@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE

)
public interface FileMapper {
    FileResponseDto toFileResponseDto(FileEntity file);
    FileEntity toFileEntity(FileConfirmDto fileConfirmDto);
}
