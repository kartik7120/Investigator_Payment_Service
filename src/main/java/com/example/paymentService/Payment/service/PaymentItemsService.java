package com.example.paymentService.Payment.service;

import com.example.paymentService.Models.PaymentItems;
import com.example.paymentService.Payment.repository.PaymentItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentItemsService {

    @Autowired
    private PaymentItemsRepository paymentItemsRepository;

    public List<PaymentItems> getByIds(List<Integer> paymentItemsIDs) {

        return paymentItemsRepository.findByIdIn(paymentItemsIDs);
    }
}
