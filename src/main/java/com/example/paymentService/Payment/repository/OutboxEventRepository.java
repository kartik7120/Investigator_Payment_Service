package com.example.paymentService.Payment.repository;


import com.example.paymentService.Models.OutboxEvent;
import com.example.paymentService.Payment.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(
            OutboxStatus status
    );
}
