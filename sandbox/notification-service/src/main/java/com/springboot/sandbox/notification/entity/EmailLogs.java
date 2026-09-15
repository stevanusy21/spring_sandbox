package com.springboot.sandbox.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.springboot.sandbox.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.UUID;

@Entity
@Table(name = "email_logs")
@EqualsAndHashCode(callSuper = false)
@Data
public class EmailLogs extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "email_to")
    private String to;
    @Column(name = "email_subject")
    private String subject;
    @Column(name = "email_body", columnDefinition = "TEXT")
    private String body;
    @Column(name = "email_status")
    private String status;
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}