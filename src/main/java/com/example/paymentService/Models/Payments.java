
package com.example.paymentService.Models;

import com.example.paymentService.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_idempotency_key",
                        columnNames = "idempotency_key"
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "payment_id", unique = true)
    private String paymentId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(unique = true, nullable = false)
    private String sessionID;

    // API idempotency key
    @Column(
            name = "idempotency_key",
            nullable = false,
            updatable = false
    )
    private String idempotencyKey;

    @OneToMany(mappedBy = "payments")
    private List<PaymentAttempts> paymentAttempts = new ArrayList<>();

    @OneToMany(mappedBy = "payment")
    private List<PaymentItems> paymentItems = new ArrayList<>();
}
