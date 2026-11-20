package com.example.paymentService.Payment.controller;

import com.dodopayments.api.core.UnwrapWebhookParams;
import com.dodopayments.api.core.http.Headers;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionResponse;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionStatus;
import com.dodopayments.api.models.payments.Payment;
import com.dodopayments.api.models.webhooks.UnwrapWebhookEvent;
import com.example.paymentService.Models.Payments;
import com.example.paymentService.Payment.DTO.PaymentCheckOutRequest;
import com.example.paymentService.Payment.KafkaTopics;
import com.example.paymentService.Payment.service.DodoClient;
import com.example.paymentService.Payment.service.KafkaProducerService;
import com.example.paymentService.Payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private DodoClient dodoClient;

    @Value("${dodo.webhook.key}")
    private String dodoWebhookSecret;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;

    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/generateIdempotentKey")
    public ResponseEntity<String> generateIdempotentKey() {
        String idempotencyKey = UUID.randomUUID().toString();
        return ResponseEntity.ok(idempotencyKey);
    }

    @PostMapping("/checkoutSessionRequest")
    public ResponseEntity<String> createPaymentCheckoutSession(@RequestBody PaymentCheckOutRequest paymentInitiateDTO, @RequestHeader("Idempotency-Key") String idempotencyKey) {

        try {

            logger.info("createPaymentCheckoutSession");

            logger.info("Request Body received: ", paymentInitiateDTO);

            Payments p = paymentService.getPaymentWithIdempotentKey(idempotencyKey);

            if (idempotencyKey == null) {
                return ResponseEntity.badRequest().body("Invalid Idempotency-Key");
            }

            if (p != null) {
                String dodoSessionID = String.valueOf(p.getSessionID());

                if (dodoSessionID == null) {
                    return ResponseEntity.badRequest().body("Session ID not found");
                }

                CheckoutSessionStatus sessionStatus = dodoClient.getClient().checkoutSessions().retrieve(dodoSessionID);

                if (sessionStatus.paymentStatus().get().toString().equals("processing")) {
                    Payment payment = dodoClient.getClient().payments().retrieve(sessionStatus.paymentId().get());

                    if (payment == null) {
                        return ResponseEntity.badRequest().body("Payment does not exists");
                    } else {
                        return ResponseEntity.status(HttpStatus.ACCEPTED).location(URI.create(payment.paymentLink().get())).build();
                    }
                }
            }

            CheckoutSessionResponse resp = dodoClient.createCheckout(paymentInitiateDTO.productCart(), paymentInitiateDTO.customer());

            logger.info("CheckoutSession Response: ", resp);

//        Create payment model and create instances of payment attempt and payment attempt history

            URI url = URI.create(resp.checkoutUrl().get());

            logger.info("CheckoutSession URL: {}", resp.checkoutUrl().orElseGet(() -> {
                return "";
            }));

//            Create a payment method with status Pending status

            paymentService.createPendingPayment(resp.sessionId(), paymentInitiateDTO.customer().email(), idempotencyKey);

            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(url).build();

        } catch (Exception e) {
            throw e;
//            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhookHandler(@RequestBody String webhookBody, @RequestHeader HttpHeaders headers) {

//        Webhooks have a 30-second timeout for both connection and read operations. Process webhooks asynchronously by returning a 200 status code immediately, then handle the event in the background.

//        Verify the webhook signature

        String webhookID = headers.getFirst("webhook-id");
        String webhookTimeStamp = headers.getFirst("webhook-timestamp");
        String webHookSignature = headers.getFirst("webhook-signature");
        logger.info("Webhook id: {}", webhookID);
        logger.info("Webhook timestamp: {}", webhookTimeStamp);
        logger.info("Webhook key: {}", dodoWebhookSecret);
        logger.info("Webhook Body: {}", webhookBody);

//        First verify the payload using the SDK helper functions

        try {
            Map<String, List<String>> headersMap = new HashMap<>();
            assert webhookID != null;
            assert webhookTimeStamp != null;
            assert webHookSignature != null;
            headersMap.put("webhook-id", List.of(webhookID));
            headersMap.put("webhook-signature", List.of(webHookSignature));
            headersMap.put("webhook-timestamp", List.of(webhookTimeStamp));
            Headers WebHookValidationHeaders = Headers.builder().putAll(headersMap).build();
            UnwrapWebhookParams params = UnwrapWebhookParams.builder().headers(
                    WebHookValidationHeaders).body(webhookBody).secret(dodoWebhookSecret).build();

            UnwrapWebhookEvent event = dodoClient.getClient().webhooks().unwrap(
                    params
            );

            boolean isPaymentSucceeded = event.isPaymentSucceeded();
            JsonNode root;
            try {
                root = objectMapper.readTree(webhookBody);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            String paymentId = root
                    .path("data")
                    .path("payment_id")
                    .asText();

            if (isPaymentSucceeded) {
//          Handle if the webhook payload is not valid
                logger.info("Payment succeeded");

//            Send the webhook object in thr producer
                logger.info("Payment ID: {}", paymentId);

                kafkaProducerService.Publish(event.toString(), KafkaTopics.PAYMENT_SUCCESS, paymentId);
            } else {
                kafkaProducerService.Publish(event.toString(), KafkaTopics.PAYMENT_FAILED, paymentId);
                logger.error("Payment not succeeded");
            }
        } catch (Exception e) {
            logger.error("Webhook Payload Invalid");

            throw new RuntimeException(e);
        }


//        Need to store this and add idempotency check since the webhook can be retried multiple times and only need to permanently store the id when all the operations are successful.
        // Handle different webhook events using Kafka

        return ResponseEntity.ok().build();
    }
}
