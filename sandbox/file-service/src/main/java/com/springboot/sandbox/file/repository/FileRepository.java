package com.springboot.sandbox.file.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.sandbox.common.enumeration.EntityType;
import com.springboot.sandbox.common.enumeration.FileCategory;
import com.springboot.sandbox.file.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long>{
    Page<FileEntity> findAllByEntityTypeAndEntityIdAndFileCategory(EntityType entityType, Long entityId, FileCategory fileCategory, Pageable pageable);
}
