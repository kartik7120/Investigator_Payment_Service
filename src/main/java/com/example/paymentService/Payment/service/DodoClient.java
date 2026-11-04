package com.example.paymentService.Payment.service;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.client.okhttp.DodoPaymentsOkHttpClient;
import com.dodopayments.api.models.checkoutsessions.*;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DodoClient {

    private DodoPaymentsClient client = DodoPaymentsOkHttpClient.builder()
            .fromEnv()
            .testMode()
            .build();

    public DodoClient(DodoPaymentsClient client) {
        this.client = client;
    }

    public CheckoutSessionResponse createCheckout(List<ProductItemReq> items) {
        CheckoutSessionRequest params = CheckoutSessionRequest.builder()
                .productCart(items)
                .returnUrl("https://yourdomain.com/return")
                .build();

        return client.checkoutSessions().create(params);
    }

}
