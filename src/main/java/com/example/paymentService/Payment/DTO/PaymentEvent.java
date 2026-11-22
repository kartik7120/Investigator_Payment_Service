package com.example.paymentService.Payment.DTO;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentEvent {

    private UUID eventId;

    private String eventType;

    private JsonNode payload;
}
