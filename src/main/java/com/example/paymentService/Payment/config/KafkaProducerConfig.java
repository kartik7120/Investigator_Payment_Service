package com.example.paymentService.Payment.config;

import com.dodopayments.api.models.webhookevents.WebhookPayload;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaProducer<String, String> createKafkaProducer() {

        Properties params = new Properties();

        params.setProperty("bootstrap.servers", "localhost:9092");
        params.setProperty("key.serializer", StringSerializer.class.getName());
        params.setProperty("value.serializer", StringSerializer.class.getName());

        params.setProperty("enable.auto.commit", "true");

        params.setProperty("auto.commit.interval.ms", "1000");

        params.setProperty("session.timeout.ms", "30000");

        return new KafkaProducer<>(params);
    }
}
