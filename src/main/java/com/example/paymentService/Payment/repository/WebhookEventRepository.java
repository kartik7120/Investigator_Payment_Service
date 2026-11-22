package com.example.paymentService.Payment.repository;

import com.example.paymentService.Models.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebhookEventRepository
        extends JpaRepository<WebhookEvent, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO webhook_events
            (webhook_id, event_type, status, payload, received_at)
        VALUES
            (:webhookId, :eventType, 'RECEIVED', :payload, NOW())
        ON CONFLICT (webhook_id) DO NOTHING
        """, nativeQuery = true)
    int insertIfNotExists(
            @Param("webhookId") String webhookId,
            @Param("eventType") String eventType,
            @Param("payload") String payload
    );
}