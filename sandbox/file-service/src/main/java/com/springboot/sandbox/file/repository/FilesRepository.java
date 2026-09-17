package com.springboot.sandbox.file.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.sandbox.common.enumeration.FileCategory;
import com.springboot.sandbox.file.entity.Files;

public interface FilesRepository extends JpaRepository<Files, Long>{
    Page<Files> findAllByUserIdAndFileCategory(Long userId, FileCategory fileCategory, Pageable pageable);
}
