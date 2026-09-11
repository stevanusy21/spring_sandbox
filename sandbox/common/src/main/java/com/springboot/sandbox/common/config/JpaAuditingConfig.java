package com.springboot.sandbox.common.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
// import org.springframework.security.core.context.SecurityContextHolder;

@Configuration 
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    AuditorAware<String> auditorProvider() {
        // String user = SecurityContextHolder.getContext().getAuthentication().getName();
        // return () -> Optional.of(user != null ? user : "SYSTEM");
        return () -> Optional.of("SYSTEM");
    }
}
