package com.example.paymentService.Payment.controller;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.client.okhttp.DodoPaymentsOkHttpClient;
import com.dodopayments.api.models.checkoutsessions.CheckoutSessionRequest;
import com.example.paymentService.Payment.DTO.PaymentCheckOutRequest;
import com.example.paymentService.Payment.DTO.PaymentIntiateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private DodoPaymentsClient dodoPaymentsClientTest = DodoPaymentsOkHttpClient.builder()
            .fromEnv()
            .testMode()
            .build();

    @PostMapping("/checkoutSessionRequest")
    public ResponseEntity createPaymentCheckoutSession(@RequestBody PaymentCheckOutRequest paymentIntiateDTO) {

        CheckoutSessionRequest params = CheckoutSessionRequest.builder()
                .addProductCart()
    }
}
