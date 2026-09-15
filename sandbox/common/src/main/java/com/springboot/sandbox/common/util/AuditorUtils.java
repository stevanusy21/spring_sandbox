package com.springboot.sandbox.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class AuditorUtils {
    public static String getCurrentUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            String usernameHeader = request.getHeader("X-User-Name");
            if (usernameHeader != null && !usernameHeader.isBlank()) {
                return usernameHeader;
            }
        }

        return "SYSTEM";
    }
}
