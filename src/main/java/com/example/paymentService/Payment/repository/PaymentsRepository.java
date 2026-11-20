package com.example.paymentService.Payment.repository;

import com.dodopayments.api.models.payments.Payment;
import com.example.paymentService.Models.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

    Payments findBySessionID(String sessionID);
    Optional<Payments> findByIdempotencyKey(String idempotencyKey);
}
