package com.springboot.sandbox.notification.internal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.springboot.sandbox.notification.entity.EmailLogs;
import com.springboot.sandbox.notification.internal.client.UserServiceFeignClient;
import com.springboot.sandbox.notification.internal.dto.request.EmailRequestDto;
import com.springboot.sandbox.notification.internal.dto.response.EmailResponseDto;
import com.springboot.sandbox.notification.internal.dto.response.UserDetailDto;
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
    private final UserServiceFeignClient userServiceFeignClient;

    @Value("${spring.mail.mail.from:no-reply@miylo.com}")
    private String senderEmail;

    public EmailResponseDto sendEmail(EmailRequestDto request) {
        UserDetailDto user = userServiceFeignClient.findUserDetailInternal(request.getUserId()).getData();

        EmailLogs emailLogs = new EmailLogs();
        emailLogs.setTo(user.getEmail());

        try {
            switch (request.getTemplate()) {
                case WELCOME:
                    emailLogs.setSubject("Welcome to this Application");
                    emailLogs.setBody(buildWelcomeEmail(user));
                    break;
                default:
                    break;
            }

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(user.getEmail());
            helper.setSubject(emailLogs.getSubject());
            helper.setText(emailLogs.getBody(), true);

            mailSender.send(mimeMessage);

            emailLogs.setStatus("SENT");
            emailLogs.setErrorMessage(null);
            log.info("Email sent successfully to {}", user.getEmail());
        } catch (Exception e) {
            emailLogs.setStatus("FAILED");
            emailLogs.setErrorMessage(e.getMessage());
            log.error("Failed to send email to {}", user.getEmail(), e);
        } finally {
            emailLogsRepository.save(emailLogs);
        }

        return EmailResponseDto.builder()
                .logId(emailLogs.getId())
                .status(emailLogs.getStatus())
                .message(emailLogs.getStatus().equals("SENT") ? "Email sent successfully" : "Email failed to send")
                .build();
    }

    public String buildWelcomeEmail(UserDetailDto user) {
        Context context = new Context();
        context.setVariable("name", user.getFullName());
        // context.setVariable("email", user.getEmail());
        return templateEngine.process("email/welcome", context);
    }
}
