package com.example.paymentService.Payment.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    private final Logger logger = Logger.getLogger(KafkaProducerService.class.getName());

    public boolean Publish(String payload, String topic, String value, String key) {

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

        Future<RecordMetadata> recordMetaData = kafkaProducer.send(
                record,
                (recordMetadata, e) -> {
                    if (e != null) {

//                            Handle the error

                        logger.log(Level.SEVERE, "Error sending record to topic: " + recordMetadata.topic(), e);
                    } else {
                        logger.info("Successfully sent message to topic: " + record.topic());

                        logger.info("""
                                
                                Key: %s,
                                Value: %s,
                                Offset: %s,
                                Timestamp: %s,
                                Partitioner: %s,
                                """.formatted(key, value, recordMetadata.offset(), recordMetadata.timestamp(), recordMetadata.partition()));

                    }
                }
        );

        return recordMetaData.isDone();
    }
}
