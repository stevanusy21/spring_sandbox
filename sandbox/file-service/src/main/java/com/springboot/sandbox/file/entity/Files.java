package com.springboot.sandbox.file.entity;

import com.springboot.sandbox.common.entity.BaseEntity;
import com.springboot.sandbox.common.enumeration.FileCategory;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "files")
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class Files extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Column(name = "file_public_url", nullable = false)
    private String filePublicUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_category", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private FileCategory fileCategory;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_description")
    private String fileDescription;

    @Column(name = "content_type", nullable = false)
    private String contentType;
}
