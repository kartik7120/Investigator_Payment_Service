package com.example.paymentService.Models;

import com.example.paymentService.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.generator.values.GeneratedValues;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "payment_id", unique = true, nullable = true)
    private String paymentId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = true, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(unique = true, nullable = false)
    private String sessionID;

    @OneToMany(mappedBy = "payments")
    private List<PaymentAttempts> paymentAttempts = new ArrayList<>();

    @OneToMany(mappedBy = "payment")
    private List<PaymentItems> paymentItems = new ArrayList<>();
}