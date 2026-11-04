package com.example.paymentService.Models;

import com.example.paymentService.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.generator.values.GeneratedValues;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    @OneToMany(mappedBy = "payments")
    private List<PaymentAttempts> paymentAttempts = new ArrayList<>();

    @OneToMany(mappedBy = "payment")
    private List<PaymentItems> paymentItems = new ArrayList<>();
}