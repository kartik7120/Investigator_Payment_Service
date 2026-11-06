package com.example.paymentService.Payment.repository;

import com.example.paymentService.Models.PaymentItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentItemsRepository extends JpaRepository<PaymentItems, Long> {

    List<PaymentItems> findByIdIn(List<Integer> ids);
}
