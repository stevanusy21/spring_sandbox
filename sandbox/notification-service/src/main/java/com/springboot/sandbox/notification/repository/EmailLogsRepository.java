package com.springboot.sandbox.notification.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.sandbox.notification.entity.EmailLogs;

public interface EmailLogsRepository extends JpaRepository<EmailLogs, UUID> {
    
}
