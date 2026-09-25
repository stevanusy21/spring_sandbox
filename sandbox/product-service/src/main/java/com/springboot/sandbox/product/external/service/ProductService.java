package com.springboot.sandbox.product.external.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.springboot.sandbox.common.dto.PageResponse;
import com.springboot.sandbox.common.exception.NotFoundException;
import com.springboot.sandbox.product.entity.ProductEntity;
import com.springboot.sandbox.product.external.dto.request.ProductCreateDto;
import com.springboot.sandbox.product.external.dto.response.ProductResponseDto;
import com.springboot.sandbox.product.repository.ProductRepository;
import com.springboot.sandbox.product.util.ProductMapper;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponseDto createProduct(ProductCreateDto request) {
        ProductEntity product = productMapper.toProductEntity(request);
        ProductEntity savedProduct = productRepository.save(product);
        return productMapper.toProductResponseDto(savedProduct);
    }

    public ProductResponseDto getProductDetail(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));
        return productMapper.toProductResponseDto(product);
    }

    public PageResponse<ProductResponseDto> getProductByPaging(
            int page,
            int pageSize,
            String sortBy,
            Direction sortDirection) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortDirection, sortBy));
        Page<ProductResponseDto> products = productRepository.findAll(pageable)
                .map(productMapper::toProductResponseDto);
        return PageResponse.from(products);

    }
}
