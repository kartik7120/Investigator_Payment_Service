package com.example.paymentService.Payment.controller;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.client.okhttp.DodoPaymentsOkHttpClient;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionRequest;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionResponse;
import com.dodopayments.api.services.blocking.invoices.PaymentService;
import com.example.paymentService.Payment.DTO.PaymentCheckOutRequest;
import com.example.paymentService.Payment.service.DodoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private DodoClient dodoClient;

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

            logger.info("CheckoutSession URL: ", url);

            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(url).build();

        } catch (Exception e) {
            throw e;
//            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
