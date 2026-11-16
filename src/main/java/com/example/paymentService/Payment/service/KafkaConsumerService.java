package com.example.paymentService.Payment.service;

import com.example.paymentService.Payment.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(
            topics = "payment.succeeded",
            groupId = "payment-service"
    )
    public void PaymentSuccessConsumer(String message) {

        log.info("Payment event received: {}", message);
    }
}
