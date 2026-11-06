package com.example.paymentService.Payment.service;

import com.example.paymentService.Models.PaymentAttempts;
import com.example.paymentService.Models.Payments;
import com.example.paymentService.Payment.repository.PaymentAttemptsRepository;
import com.example.paymentService.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentAttemptsService {

    @Autowired
    private PaymentAttemptsRepository paymentAttemptsRepository;

    @Autowired
    private PaymentService paymentService;

    public PaymentAttempts createPaymentAttempt(String sessionID, String payment_provider_id) {

        PaymentAttempts paymentAttempts = new PaymentAttempts();

        Payments payments = paymentService.getPayment(sessionID);

        paymentAttempts.setPayments(payments);
        paymentAttempts.setAttempt_number(1);
        paymentAttempts.setStatus(payments.getPaymentStatus());
        paymentAttempts.setProvider_payment_id(payment_provider_id);

        return paymentAttemptsRepository.save(paymentAttempts);
    }
}
