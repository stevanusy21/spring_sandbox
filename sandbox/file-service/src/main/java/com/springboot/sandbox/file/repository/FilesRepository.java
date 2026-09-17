package com.springboot.sandbox.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.sandbox.file.entity.Files;

public interface FilesRepository extends JpaRepository<Files, Long>{
    
}
