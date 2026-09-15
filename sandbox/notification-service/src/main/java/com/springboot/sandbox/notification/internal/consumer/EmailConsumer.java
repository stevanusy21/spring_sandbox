package com.springboot.sandbox.notification.internal.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.springboot.sandbox.notification.config.RabbitMqConfig;
import com.springboot.sandbox.notification.internal.dto.request.EmailRequestDto;
import com.springboot.sandbox.notification.internal.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor 
public class EmailConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
    public void consumeEmailMessage(EmailRequestDto emailRequestDto) {
        log.info("Received email request for userId: {}", emailRequestDto.getUserId());
        emailService.sendEmail(emailRequestDto);
    }
}
