package com.example.paymentService.Models;

//https://chatgpt.com/c/6ae8ae55-d8d0-83ee-805d-e8e0312c4fa3

import com.example.paymentService.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentAttempts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "payments_id",
            referencedColumnName = "id"
    )
    private Payments payments;

    private int attempt_number;

    private String provider_payment_id;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;
}
