package com.example.paymentService.Models;


import com.example.paymentService.Payment.WebhookStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "webhook_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_webhook_id",
                        columnNames = "webhook_id"
                )
        }
)
@Getter
@Setter
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "webhook_id", nullable = false, unique = true)
    private String webhookId;

    private String eventType;

    @Enumerated(EnumType.STRING)
    private WebhookStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime receivedAt;

    private LocalDateTime processedAt;
}
