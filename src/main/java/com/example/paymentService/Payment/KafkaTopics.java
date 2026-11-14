package com.example.paymentService.Payment;

import lombok.*;

@Getter
@NoArgsConstructor
public enum KafkaTopics {

    PAYMENT_SUCCESS("payment.succeeded"),
    PAYMENT_FAILED("payment.failed");

    String kafkaTopicName;

    KafkaTopics(String s) {
        this.kafkaTopicName = s;
    }
}
