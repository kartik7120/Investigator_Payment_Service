package com.example.paymentService.Payment.service;

import com.dodopayments.api.models.products.Product;
import com.example.paymentService.Models.PaymentItems;
import com.example.paymentService.Models.Payments;
import com.example.paymentService.Payment.DTO.DodoWebhookDTO;
import com.example.paymentService.Payment.DTO.PaymentFailedWebhookDTO;
import com.example.paymentService.Payment.KafkaTopics;
import com.example.paymentService.PaymentStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KafkaConsumerService {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentAttemptsService paymentAttemptsService;

    @Autowired
    private PaymentItemsService paymentItemsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DodoClient dodoClient;

    @KafkaListener(
            topics = "payment.succeeded",
            groupId = "payment-service"
    )
    @Transactional
    public void PaymentSuccessConsumer(String message, Acknowledgment acknowledgment) {

        log.info("Payment event received: {}", message);

//        Need to parse the object in java object

        DodoWebhookDTO webhook;
        try {
            webhook = objectMapper.readValue(message, DodoWebhookDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Product> products = new ArrayList<>();

        for (DodoWebhookDTO.ProductCart productCart : webhook.getData().getProductCart()) {
            Product product = dodoClient.getClient().products().retrieve(productCart.getProductId());
            products.add(product);
        }

        List<PaymentItems> paymentItemsArr = new ArrayList<>();

        for (Product product : products) {

            PaymentItems paymentItems = new PaymentItems();

            paymentItems.setProductName(product.name().get());
            paymentItems.setPrice(new BigDecimal(product.price().toString()));
            paymentItems.setQuantity(1);
            paymentItems.setTotalAmount(new BigDecimal(product.price().toString()));
            paymentItems.setSessionID(webhook.getData().getCheckoutSessionId());

            paymentItemsService.createPaymentItem(paymentItems);

            paymentItemsArr.add(paymentItems);
        }

//        paymentService.createPayment(
//                webhook.getData().getCheckoutSessionId(), paymentItemsArr.stream().map(e -> e.getId().intValue()).toList(), webhook.getData().getCustomer().getEmail());

//        Payments payment = paymentService.getPayment(webhook.getData().getCheckoutSessionId());

//        Payment attempts come after creation of the payment object.
        paymentAttemptsService.createPaymentAttempt(webhook.getData().getCheckoutSessionId(), webhook.getData().getPaymentId());

        paymentService.updatePaymentStatus(webhook.getData().getCheckoutSessionId(), PaymentStatus.SUCCESS);

        paymentService.update(
                webhook.getData().getCheckoutSessionId(), paymentItemsArr.stream().map(e -> e.getId().intValue()).toList()
        );

        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "payment.failed",
            groupId = "payment-service"
    )
    @Transactional
    public void PaymentFailedConsumer(String message, Acknowledgment acknowledgment) {

        log.info("Payment failed event received: {}", message);

        PaymentFailedWebhookDTO webhookDTO;

        try {
            webhookDTO = objectMapper.readValue(message, PaymentFailedWebhookDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Product> products = new ArrayList<>();

        for (PaymentFailedWebhookDTO.ProductCart productCart : webhookDTO.getData().getProductCart()) {
            Product product = dodoClient.getClient().products().retrieve(productCart.getProductId());
            products.add(product);
        }

        List<PaymentItems> paymentItemsArr = new ArrayList<>();

        for (Product product : products) {

            PaymentItems paymentItems = new PaymentItems();

            paymentItems.setProductName(product.name().get());
            paymentItems.setPrice(new BigDecimal(product.price().toString()));
            paymentItems.setQuantity(1);
            paymentItems.setTotalAmount(new BigDecimal(product.price().toString()));
            paymentItems.setSessionID(webhookDTO.getData().getCheckoutSessionId());

            paymentItemsService.createPaymentItem(paymentItems);

            paymentItemsArr.add(paymentItems);
        }

//        Payments payment = paymentService.getPayment(webhookDTO.getData().getCheckoutSessionId());

        paymentAttemptsService.createPaymentAttempt(webhookDTO.getData().getCheckoutSessionId(), webhookDTO.getData().getPaymentId());

        paymentService.updatePaymentStatus(webhookDTO.getData().getCheckoutSessionId(), PaymentStatus.FAILED);

        paymentService.update(
                webhookDTO.getData().getCheckoutSessionId(), paymentItemsArr.stream().map(e -> e.getId().intValue()).toList()
        );

        acknowledgment.acknowledge();
    }
}
