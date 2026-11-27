package com.example.paymentService.Payment.repository;

import com.example.paymentService.Models.Payments;
import com.example.paymentService.PaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

    Payments findBySessionID(String sessionID);
    Optional<Payments> findByIdempotencyKey(String idempotencyKey);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Payments p SET p.paymentStatus = :status WHERE p.sessionID = :sessionID")
    void updatePaymentStatus(@Param("sessionID") String sessionID, @Param("status") PaymentStatus paymentStatus);
}
