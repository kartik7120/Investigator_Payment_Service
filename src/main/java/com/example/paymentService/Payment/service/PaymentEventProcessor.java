package com.example.paymentService.Payment.service;


import com.dodopayments.api.models.products.Product;
import com.example.paymentService.Models.PaymentItems;
import com.example.paymentService.Payment.DTO.DodoWebhookDTO;
import com.example.paymentService.Payment.DTO.PaymentEvent;
import com.example.paymentService.Payment.DTO.PaymentFailedWebhookDTO;
import com.example.paymentService.Payment.repository.ProcessedEventRepository;
import com.example.paymentService.PaymentStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentEventProcessor {

    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    private final Logger log = Logger.getLogger(PaymentEventProcessor.class.getName());

    private final PaymentItemsService paymentItemsService;
    private final PaymentAttemptsService paymentAttemptsService;
    private final PaymentService paymentService;

    @Autowired
    private DodoClient dodoClient;

    @Transactional
    public void processPaymentSuccess(PaymentEvent event) {

        // 1. Register the event atomically
        int inserted = processedEventRepository.insertIfNotExists(
                event.getEventId(),
                event.getEventType()
        );

        // 2. If event already exists, skip processing
        if (inserted == 0) {
            log.info("Duplicate event ignored: " + event.getEventId());
            return;
        }

        // 3. Parse original DodoPayments payload
        DodoWebhookDTO webhook;

        try {
            webhook = objectMapper.treeToValue(
                    event.getPayload(),
                    DodoWebhookDTO.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid payment event", e);
        }

        // 4. Execute your existing payment business logic

        List<Product> products = new ArrayList<>();

        for (DodoWebhookDTO.ProductCart productCart :
                webhook.getData().getProductCart()) {

            Product product = dodoClient.getClient()
                    .products()
                    .retrieve(productCart.getProductId());

            products.add(product);
        }

        List<PaymentItems> paymentItemsArr = new ArrayList<>();

        for (Product product : products) {

            PaymentItems paymentItems = new PaymentItems();

            paymentItems.setProductName(product.name().get());
            paymentItems.setPrice(
                    new BigDecimal(product.price().toString())
            );
            paymentItems.setQuantity(1);
            paymentItems.setTotalAmount(
                    new BigDecimal(product.price().toString())
            );
            paymentItems.setSessionID(
                    webhook.getData().getCheckoutSessionId()
            );

            paymentItemsService.createPaymentItem(paymentItems);

            paymentItemsArr.add(paymentItems);
        }

        // 5. Create payment attempt
        paymentAttemptsService.createPaymentAttempt(
                webhook.getData().getCheckoutSessionId(),
                webhook.getData().getPaymentId()
        );

        // 6. Update payment status
        paymentService.updatePaymentStatus(
                webhook.getData().getCheckoutSessionId(),
                PaymentStatus.SUCCESS
        );

        // 7. Associate payment items
        paymentService.update(
                webhook.getData().getCheckoutSessionId(),
                paymentItemsArr.stream()
                        .map(e -> e.getId().intValue())
                        .toList()
        );
    }


    @Transactional
    public void processPaymentFailed(PaymentEvent event) {

        // 1. Check whether this event was already processed
        int inserted = processedEventRepository.insertIfNotExists(
                event.getEventId(),
                event.getEventType()
        );

        if (inserted == 0) {
            log.info("Duplicate payment.failed event ignored: "+
                    event.getEventId());
            return;
        }

        // 2. Deserialize original webhook payload
        PaymentFailedWebhookDTO webhookDTO;

        try {
            webhookDTO = objectMapper.treeToValue(
                    event.getPayload(),
                    PaymentFailedWebhookDTO.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Failed to deserialize payment.failed event", e
            );
        }

        // 3. Retrieve products
        List<Product> products = new ArrayList<>();

        for (PaymentFailedWebhookDTO.ProductCart productCart :
                webhookDTO.getData().getProductCart()) {

            Product product = dodoClient.getClient()
                    .products()
                    .retrieve(productCart.getProductId());

            products.add(product);
        }

        // 4. Create payment items
        List<PaymentItems> paymentItemsArr = new ArrayList<>();

        for (Product product : products) {

            PaymentItems paymentItems = new PaymentItems();

            paymentItems.setProductName(product.name().get());

            paymentItems.setPrice(
                    new BigDecimal(product.price().toString())
            );

            paymentItems.setQuantity(1);

            paymentItems.setTotalAmount(
                    new BigDecimal(product.price().toString())
            );

            paymentItems.setSessionID(
                    webhookDTO.getData().getCheckoutSessionId()
            );

            paymentItemsService.createPaymentItem(paymentItems);

            paymentItemsArr.add(paymentItems);
        }

        // 5. Create payment attempt
        paymentAttemptsService.createPaymentAttempt(
                webhookDTO.getData().getCheckoutSessionId(),
                webhookDTO.getData().getPaymentId()
        );

        // 6. Update payment status
        paymentService.updatePaymentStatus(
                webhookDTO.getData().getCheckoutSessionId(),
                PaymentStatus.FAILED
        );

        // 7. Associate payment items
        paymentService.update(
                webhookDTO.getData().getCheckoutSessionId(),
                paymentItemsArr.stream()
                        .map(e -> e.getId().intValue())
                        .toList()
        );
    }

}
