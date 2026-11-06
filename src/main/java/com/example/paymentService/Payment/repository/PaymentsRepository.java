package com.example.paymentService.Payment.repository;

import com.example.paymentService.Models.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

    Payments findBySessionID(String sessionID);
}
