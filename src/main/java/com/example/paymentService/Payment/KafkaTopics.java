package com.example.paymentService.Payment;

import lombok.*;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum KafkaTopics {

    PAYMENT_SUCCESS("payment.succeeded"),
    PAYMENT_FAILED("payment.failed");

    private final String kafkaTopicName;

    public static KafkaTopics fromEventType(String eventType) {
        return Arrays.stream(values())
                .filter(topic -> topic.kafkaTopicName.equals(eventType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown Kafka event type: " + eventType
                ));
    }
}