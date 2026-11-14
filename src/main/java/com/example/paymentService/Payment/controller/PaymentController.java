package com.example.paymentService.Payment.controller;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.client.okhttp.DodoPaymentsOkHttpClient;
import com.dodopayments.api.core.UnwrapWebhookParams;
import com.dodopayments.api.core.http.Headers;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionRequest;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionResponse;
import com.dodopayments.api.models.webhooks.UnwrapWebhookEvent;
import com.dodopayments.api.services.blocking.invoices.PaymentService;
import com.example.paymentService.Payment.DTO.PaymentCheckOutRequest;
import com.example.paymentService.Payment.service.DodoClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private DodoClient dodoClient;

    @Value("dodo.webhook.key")
    private String dodoWebhookKey;

    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/checkoutSessionRequest")
    public ResponseEntity<String> createPaymentCheckoutSession(@RequestBody PaymentCheckOutRequest paymentInitiateDTO) {

        try {

            logger.info("createPaymentCheckoutSession");

            logger.info("Request Body received: ", paymentInitiateDTO);

            CheckoutSessionResponse resp = dodoClient.createCheckout(paymentInitiateDTO.productCart(), paymentInitiateDTO.customer());

            logger.info("CheckoutSession Response: ", resp);

//        Create payment model and create instances of payment attempt and payment attempt history

            URI url = URI.create(resp.checkoutUrl().get());

            logger.info("CheckoutSession URL: {}", resp.checkoutUrl().orElseGet(() -> {
                return "";
            }));

            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(url).build();

        } catch (Exception e) {
            throw e;
//            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public void webhookHandler(@RequestBody JsonNode webhook, @RequestHeader HttpHeaders headers) {

//        Verify the webhook signature

        String webhookID = headers.getFirst("webhook-id");
        String webhookTimeStamp = headers.getFirst("webhook-timestamp");

        logger.info("Webhook id: {}", webhookID);
        logger.info("Webhook timestamp: {}", webhookTimeStamp);
        logger.info("Webhook key: {}", dodoWebhookKey);

//        First verify the payload using the SDK helper functions

        Map<String, List<String>> headersMap = new HashMap<>();
        assert webhookID != null;
        assert webhookTimeStamp != null;
        headersMap.put("webhook-id", List.of(webhookID));
        headersMap.put("webhook-signature", List.of(dodoWebhookKey));
        headersMap.put("webhook-timestamp", List.of(webhookTimeStamp));
        Headers WebHookValidationHeaders = Headers.builder().putAll(headersMap).build();
        UnwrapWebhookParams params = UnwrapWebhookParams.builder().headers(
                WebHookValidationHeaders).build();

        UnwrapWebhookEvent event = dodoClient.getClient().webhooks().unwrap(
            params
        );

        boolean isWebHookPayloadValid = event.isValid();

        if (isWebHookPayloadValid) {
//          Handle if the webhook payload is not valid

            System.out.println("Webhook Payload Valid");
        } else {
            System.out.println("Webhook payload is not valid");
        }

//        Need to store this and add idempotency check since the webhook can be retried multiple times and only need to permanently store the id when all the operations are successful.

        String eventType = webhook.get("type").asText();

        System.out.println("Webhook event type: " + eventType);

        System.out.println("Webhook Event: " + webhook.toPrettyString());

        // Handle different webhook events
    }
}
