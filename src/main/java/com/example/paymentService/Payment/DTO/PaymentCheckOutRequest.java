package com.example.paymentService.Payment.DTO;

import com.dodopayments.api.models.customers.Customer;
import com.dodopayments.api.models.payments.Payment;

public record PaymentCheckOutRequest(
        Payment.ProductCart productCart,
        Customer customer
) {
}
