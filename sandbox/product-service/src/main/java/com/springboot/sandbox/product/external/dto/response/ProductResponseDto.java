package com.springboot.sandbox.product.external.dto.response;

import com.springboot.sandbox.common.enumeration.ProductStatus;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private ProductStatus status;
}
