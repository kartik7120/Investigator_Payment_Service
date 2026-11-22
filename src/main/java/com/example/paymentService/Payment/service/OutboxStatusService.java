package com.example.paymentService.Payment.service;


import com.example.paymentService.Models.OutboxEvent;
import com.example.paymentService.Payment.OutboxStatus;
import com.example.paymentService.Payment.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxStatusService {

    private final OutboxEventRepository outboxRepository;

    @Transactional
    public void markAsPublished(UUID id) {

        OutboxEvent event = outboxRepository
                .findById(id)
                .orElseThrow();

        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
        event.setAttempts(event.getAttempts() + 1);
    }

    @Transactional
    public void incrementAttempts(UUID id) {

        OutboxEvent event = outboxRepository
                .findById(id)
                .orElseThrow();

        event.setAttempts(event.getAttempts() + 1);
    }
}
