package com.example.paymentService.Models;

import com.example.paymentService.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_attempt_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentAttemptHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "payment_attempt_id",
            referencedColumnName = "id",
            nullable = false
    )
    private PaymentAttempts paymentAttempt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus state;

    private String reason;

    @CreationTimestamp
    private LocalDateTime createdAt;
}