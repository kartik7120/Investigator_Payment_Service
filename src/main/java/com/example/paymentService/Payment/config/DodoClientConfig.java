package com.example.paymentService.Payment.config;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.client.okhttp.DodoPaymentsOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DodoClientConfig {

    @Bean
    public DodoPaymentsClient dodoPaymentsClient(
            @Value("${dodo.token}") String token) {

        return DodoPaymentsOkHttpClient.builder()
                .testMode()
                .bearerToken(token)
                .build();
    }
}