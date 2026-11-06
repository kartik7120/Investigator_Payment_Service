package com.example.paymentService.Payment.DTO;

import com.dodopayments.api.models.customers.Customer;
import com.dodopayments.api.models.payments.Payment;

import java.util.List;

public record PaymentCheckOutRequest(
        List<ProductDTO> productCart,
        CustomerDTO customer
) {
}
