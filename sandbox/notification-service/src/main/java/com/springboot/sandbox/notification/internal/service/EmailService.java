package com.springboot.sandbox.notification.internal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.springboot.sandbox.notification.entity.EmailLogs;
import com.springboot.sandbox.notification.internal.dto.request.EmailRequestDto;
import com.springboot.sandbox.notification.internal.dto.response.EmailResponseDto;
import com.springboot.sandbox.notification.repository.EmailLogsRepository;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final EmailLogsRepository emailLogsRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.mail.from:no-reply@miylo.com}")
    private String senderEmail;

    public EmailResponseDto sendEmail(EmailRequestDto request) {
        EmailLogs emailLogs = new EmailLogs();
        emailLogs.setTo(request.getEmail());

        try {
            switch (request.getTemplate()) {
                case WELCOME:
                    emailLogs.setSubject("Welcome to this Application");
                    emailLogs.setBody(buildWelcomeEmail(request));
                    break;
                case RESET_PASSWORD:
                    emailLogs.setSubject("Reset Password");
                    emailLogs.setBody(buildResetPasswordEmail(request));
                    break;
                default:
                    break;
            }

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(request.getEmail());
            helper.setSubject(emailLogs.getSubject());
            helper.setText(emailLogs.getBody(), true);

            mailSender.send(mimeMessage);

            emailLogs.setStatus("SENT");
            emailLogs.setErrorMessage(null);
            log.info("Email sent successfully to {}", request.getEmail());
        } catch (Exception e) {
            emailLogs.setStatus("FAILED");
            emailLogs.setErrorMessage(e.getMessage());
            log.error("Failed to send email to {}", request.getEmail(), e);
        } finally {
            emailLogsRepository.save(emailLogs);
        }

        return EmailResponseDto.builder()
                .logId(emailLogs.getId())
                .status(emailLogs.getStatus())
                .message(emailLogs.getStatus().equals("SENT") ? "Email sent successfully" : "Email failed to send")
                .build();
    }

    public String buildWelcomeEmail(EmailRequestDto request) {
        Context context = new Context();
        context.setVariable("name", request.getFullName());
        // context.setVariable("email", user.getEmail());
        return templateEngine.process("email/welcome", context);
    }

    public String buildResetPasswordEmail(EmailRequestDto request) {
        Context context = new Context();
        context.setVariable("name", request.getFullName());
        // context.setVariable("email", user.getEmail());
        return templateEngine.process("email/reset-password", context);
    }
}
