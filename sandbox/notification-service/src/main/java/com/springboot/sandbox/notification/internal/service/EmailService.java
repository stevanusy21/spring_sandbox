package com.springboot.sandbox.notification.internal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.springboot.sandbox.notification.entity.EmailLogs;
import com.springboot.sandbox.notification.internal.dto.request.EmailRequestDto;
import com.springboot.sandbox.notification.internal.dto.response.EmailResponseDto;
import com.springboot.sandbox.notification.repository.EmailLogsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor 
@Slf4j
public class EmailService {
    private final EmailLogsRepository emailLogsRepository;
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.mail.from:no-reply@miylo.com}")
    private String senderEmail;

    public EmailResponseDto sendEmail(EmailRequestDto request) {
        EmailLogs emailLogs = new EmailLogs();
        emailLogs.setTo(request.getTo());
        emailLogs.setSubject(request.getSubject());
        emailLogs.setBody(request.getBody());
        
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(request.getTo());
            mailMessage.setSubject(request.getSubject());
            mailMessage.setText(request.getBody());
            
            mailSender.send(mailMessage);
            emailLogs.setStatus("SENT");
            emailLogs.setErrorMessage(null);
            log.info("Email sent successfully to {}", request.getTo());
        } catch (Exception e) {
            emailLogs.setStatus("FAILED");
            emailLogs.setErrorMessage(e.getMessage());
            log.error("Failed to send email to {}", request.getTo(), e);
        } finally {
            emailLogsRepository.save(emailLogs);
        }

        return EmailResponseDto.builder()
            .logId(emailLogs.getId())
            .status(emailLogs.getStatus())
            .message(emailLogs.getStatus().equals("SENT") ? "Email sent successfully" : "Email failed to send")
            .build();
    }
}
