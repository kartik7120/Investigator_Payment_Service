package com.example.paymentService.Payment.service;


import com.example.paymentService.Models.OutboxEvent;
import com.example.paymentService.Payment.OutboxStatus;
import com.example.paymentService.Payment.repository.OutboxEventRepository;
import com.example.paymentService.Payment.repository.WebhookEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookEventRepository webhookRepository;
    private final OutboxEventRepository outboxRepository;

    @Transactional
    public boolean saveWebhook(
            String webhookId,
            String eventType,
            String payload) {

        // 1. Insert webhook into database
        int inserted = webhookRepository.insertIfNotExists(
                webhookId,
                eventType,
                payload
        );

        // 2. Ignore duplicate webhook
        if (inserted == 0) {
            return false;
        }

        // 3. Create corresponding outbox event
        OutboxEvent event = new OutboxEvent();

        event.setWebhookId(webhookId);
        event.setEventType(eventType);
        event.setPayload(payload);
        event.setStatus(OutboxStatus.PENDING);
        event.setCreatedAt(LocalDateTime.now());
        event.setAttempts(0);

        // 4. Save outbox event
        outboxRepository.save(event);

        return true;
    }
}
