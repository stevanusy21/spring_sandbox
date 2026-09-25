package com.springboot.sandbox.product.util;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.springboot.sandbox.product.entity.ProductEntity;
import com.springboot.sandbox.product.external.dto.request.ProductCreateDto;
import com.springboot.sandbox.product.external.dto.response.ProductResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductEntity toProductEntity(ProductCreateDto productCreateDto);
    ProductResponseDto toProductResponseDto(ProductEntity productEntity);
}
