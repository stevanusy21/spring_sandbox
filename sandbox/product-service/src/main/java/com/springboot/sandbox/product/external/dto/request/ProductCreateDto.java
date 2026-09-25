package com.springboot.sandbox.product.external.dto.request;

import com.springboot.sandbox.common.enumeration.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCreateDto {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Product price is required")
    private Double price;

    @NotNull(message = "Product stock is required")
    private Integer stock;
    
    @NotNull(message = "Product status is required")
    private ProductStatus status;
}
