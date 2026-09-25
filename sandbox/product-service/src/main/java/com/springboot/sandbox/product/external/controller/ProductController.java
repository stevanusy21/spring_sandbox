package com.springboot.sandbox.product.external.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.common.dto.PageResponse;
import com.springboot.sandbox.product.external.dto.request.ProductCreateDto;
import com.springboot.sandbox.product.external.dto.response.ProductResponseDto;
import com.springboot.sandbox.product.external.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("")
    public ApiResponse<ProductResponseDto> createProduct(@RequestBody @Valid ProductCreateDto request) {
        return ApiResponse.success(productService.createProduct(request), "Product created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponseDto> getProductDetail(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductDetail(id), "Product details retrieved successfully");
    }

    @GetMapping("")
    public PageResponse<ProductResponseDto> findProductByPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "DESC") Direction sortDirection) {
        return productService.getProductByPaging(page, pageSize, sortBy, sortDirection);
    }

}
