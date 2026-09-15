package com.springboot.sandbox.common.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.springboot.sandbox.common.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration 
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@RequiredArgsConstructor
public class JpaAuditingConfig {
    private final JwtUtil jwtUtil;

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                String usernameHeader = request.getHeader("X-User-Name");
                if (usernameHeader != null && !usernameHeader.isBlank()) {
                    return Optional.of(usernameHeader);
                }
            
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    if (jwtUtil.isTokenValid(token)) {
                        return Optional.ofNullable(jwtUtil.extractUsername(token));
                    }
                }
            }
            
            return Optional.of("SYSTEM");
        };
    }
}
