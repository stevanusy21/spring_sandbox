package com.springboot.sandbox.notification.internal.dto.request;

import lombok.Data;

@Data
public class EmailRequestDto {
    private String to;
    private String subject;
    private String body;
}
