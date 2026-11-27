package com.example.paymentService.Payment.service;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.models.payments.Payment;
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
import java.util.Optional;

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
            String emailID, String idempotentKey) {

        List<PaymentItems> paymentItems = paymentItemsService.getByIds(paymentItemsIDs);

        if (paymentItems.size() != paymentItemsIDs.size()) {
            throw new IllegalArgumentException("Some payment items were not found");
        }

        BigDecimal totalAmount = paymentItems.stream()
                .map(PaymentItems::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payments payments = paymentsRepository.findByIdempotencyKey(idempotentKey).orElse(null);

        if (payments == null) {
            payments = new Payments();
        }

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

    public Payments createPendingPayment(String sessionID, String emailID, String idempotencyKey) {
        Payments payments = new Payments();
        payments.setSessionID(sessionID);
        payments.setPaymentStatus(PaymentStatus.PENDING);
        payments.setEmail(emailID);
        payments.setIdempotencyKey(idempotencyKey);
        paymentsRepository.save(payments);
        return payments;
    }

    public Payments getPaymentUsingSessionID(String sessionID) {

        return paymentsRepository.findBySessionID(sessionID);
    }

    @Transactional
    public void UpdatePaymentStatus(String sessionID, PaymentStatus paymentStatus) {

        Payments payment = paymentsRepository.findBySessionID(sessionID);

        System.out.println("Updating payment status for sessionID: " + sessionID);

        System.out.println("Updating this payment object : " + payment);

        paymentsRepository.updatePaymentStatus(sessionID, paymentStatus);
    }


    public void update(String checkoutSessionId, List<Integer> paymentItemsIDs) {
        Payments payment = paymentsRepository.findBySessionID(checkoutSessionId);

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        List<PaymentItems> paymentItems = paymentItemsService.getByIds(paymentItemsIDs);

        if (paymentItems.size() != paymentItemsIDs.size()) {
            throw new IllegalArgumentException("Some payment items were not found");
        }

        BigDecimal totalAmount = paymentItems.stream()
                .map(PaymentItems::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        payment.setSessionID(checkoutSessionId);
        payment.setPaymentItems(paymentItems);
        payment.setAmount(totalAmount);

        paymentsRepository.save(payment);
        payment.setPaymentItems(paymentItems);
        paymentsRepository.save(payment);
    }

    public Payments getPaymentWithIdempotentKey(String idempotencyKey) {
        Optional<Payments> p = paymentsRepository.findByIdempotencyKey(idempotencyKey);

        return p.orElse(null);

    }

    public PaymentStatus getPaymentStatus(String idempotentKey) {

        return paymentsRepository.findBySessionID(idempotentKey).getPaymentStatus();
    }

    public void createPaymentWithIdempotentKey(String idempotencyKey) {

        Payments payment = new Payments();

        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setIdempotencyKey(idempotencyKey);
        payment.setAmount(new BigDecimal(0));
        payment.setSessionID("");
        paymentsRepository.save(payment);
    }
}
