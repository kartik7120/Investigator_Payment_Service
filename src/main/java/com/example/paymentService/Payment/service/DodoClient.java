package com.example.paymentService.Payment.service;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.client.okhttp.DodoPaymentsOkHttpClient;
import com.dodopayments.api.models.checkoutsessions.*;

import java.util.List;

import com.dodopayments.api.models.customers.Customer;
import com.dodopayments.api.models.payments.NewCustomer;
import com.dodopayments.api.models.products.Product;
import com.example.paymentService.Models.PaymentAttempts;
import com.example.paymentService.Payment.DTO.CustomerDTO;
import com.example.paymentService.Payment.DTO.ProductDTO;
import jakarta.transaction.Transactional;
import kotlin.Pair;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "dodo")
@Getter
@Setter
public class DodoClient {

    @Autowired
    private ProductService productService;

    private String token;

    @Autowired
    private DodoPaymentsClient client;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentAttemptsService paymentAttemptsService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    public CheckoutSessionResponse createCheckout(List<ProductDTO> items, CustomerDTO customer) {

        Pair<List<Product>, List<Integer>> temp = productService.createProducts(items);

        List<Product> products = temp.getFirst();
        List<Integer> paymentItems = temp.getSecond();

        List<ProductItemReq> productCart = products.stream()
                .map(product -> ProductItemReq.builder()
                        .productId(product._productId())
                        .quantity(1)
                        .build())
                .toList();

        NewCustomer ct = NewCustomer.builder().email(customer.email()).phoneNumber(customer.phoneNumber()).build();

        CheckoutSessionRequest params = CheckoutSessionRequest.builder()
                .productCart(productCart)
                .returnUrl("https://yourdomain.com/return")
                .customer(ct)
                .build();

        CheckoutSessionResponse resp = client.checkoutSessions().create(params);

        logger.info("CheckoutSession Response: {}", resp.toString());

        paymentService.createPayment(resp.sessionId(), paymentItems, customer.email());

//        Create PaymentAttempt and PaymentAttemptHistory

        paymentAttemptsService.createPaymentAttempt(resp.sessionId(), resp.paymentId().orElse(""));

        return resp;
    }
}
