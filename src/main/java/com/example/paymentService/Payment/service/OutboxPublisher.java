package com.example.paymentService.Payment.service;


import com.example.paymentService.Models.OutboxEvent;
import com.example.paymentService.Payment.KafkaTopics;
import com.example.paymentService.Payment.OutboxStatus;
import com.example.paymentService.Payment.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxRepository;
    private final OutboxStatusService statusService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Scheduled(fixedDelay = 2000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(
                        OutboxStatus.PENDING
                );

        for (OutboxEvent event : events) {

            try {

                kafkaProducerService.Publish(
                        event.getPayload(),
                        KafkaTopics.valueOf(event.getEventType()),
                        event.getId().toString()
                );

//                kafkaTemplate.send(
//                        "payment-events",
//                        event.getId().toString(),
//                        event.getPayload()
//                ).get(10, TimeUnit.SECONDS);

                statusService.markAsPublished(event.getId());

            } catch (Exception e) {

                log.error(
                        "Failed to publish event: {}",
                        event.getId(),
                        e
                );

                statusService.incrementAttempts(event.getId());
            }
        }
    }
}
