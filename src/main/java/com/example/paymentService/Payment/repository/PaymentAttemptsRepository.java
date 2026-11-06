package com.example.paymentService.Payment.repository;

import com.example.paymentService.Models.PaymentAttempts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAttemptsRepository extends JpaRepository<PaymentAttempts, Long> {


}
