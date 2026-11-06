package com.example.paymentService.Payment.service;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.example.paymentService.Models.PaymentItems;
import com.example.paymentService.Models.Payments;
import com.example.paymentService.Payment.repository.PaymentsRepository;
import com.example.paymentService.PaymentStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private DodoPaymentsClient dodoClient;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private PaymentItemsService paymentItemsService;

    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Transactional
    public void createPayment(
            String sessionID,
            List<Integer> paymentItemsIDs,
            String emailID) {

        List<PaymentItems> paymentItems = paymentItemsService.getByIds(paymentItemsIDs);

        if (paymentItems.size() != paymentItemsIDs.size()) {
            throw new IllegalArgumentException("Some payment items were not found");
        }

        BigDecimal totalAmount = paymentItems.stream()
                .map(PaymentItems::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payments payments = new Payments();

        payments.setSessionID(sessionID);
        payments.setPaymentItems(paymentItems);
        payments.setPaymentStatus(PaymentStatus.PENDING);
        payments.setEmail(emailID);
        payments.setAmount(totalAmount);

        paymentsRepository.save(payments);
    }

    public Payments getPayment(String sessionID) {

        return paymentsRepository.findBySessionID(sessionID);
    }
}
