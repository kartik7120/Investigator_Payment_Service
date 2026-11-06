package com.example.paymentService.Payment.DTO;

public record ProductDTO (
        int price,
        String productID,
        int quantity,
        String name
) {
}
