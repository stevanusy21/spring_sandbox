package com.springboot.sandbox.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.sandbox.product.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
}
