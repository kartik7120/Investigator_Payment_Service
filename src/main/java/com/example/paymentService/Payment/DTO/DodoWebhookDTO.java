package com.example.paymentService.Payment.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DodoWebhookDTO {

    @JsonProperty("business_id")
    private String businessId;

    private PaymentData data;

    private Instant timestamp;

    private String type;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentData {

        private Billing billing;

        @JsonProperty("brand_id")
        private String brandId;

        @JsonProperty("business_id")
        private String businessId;

        @JsonProperty("card_holder_name")
        private String cardHolderName;

        @JsonProperty("card_issuing_country")
        private String cardIssuingCountry;

        @JsonProperty("card_last_four")
        private String cardLastFour;

        @JsonProperty("card_network")
        private String cardNetwork;

        @JsonProperty("card_type")
        private String cardType;

        @JsonProperty("checkout_session_id")
        private String checkoutSessionId;

        @JsonProperty("created_at")
        private Instant createdAt;

        private String currency;

        @JsonProperty("custom_field_responses")
        private Object customFieldResponses;

        private Customer customer;

        @JsonProperty("digital_products_delivered")
        private Boolean digitalProductsDelivered;

        @JsonProperty("discount_id")
        private String discountId;

        private Object discounts;

        private List<Object> disputes;

        @JsonProperty("error_code")
        private String errorCode;

        @JsonProperty("error_message")
        private String errorMessage;

        @JsonProperty("invoice_id")
        private String invoiceId;

        @JsonProperty("invoice_url")
        private String invoiceUrl;

        @JsonProperty("is_update_payment_method")
        private Boolean isUpdatePaymentMethod;

        private Map<String, Object> metadata;

        @JsonProperty("payload_type")
        private String payloadType;

        @JsonProperty("payment_id")
        private String paymentId;

        @JsonProperty("payment_link")
        private String paymentLink;

        @JsonProperty("payment_method")
        private String paymentMethod;

        @JsonProperty("payment_method_id")
        private String paymentMethodId;

        @JsonProperty("payment_method_type")
        private String paymentMethodType;

        @JsonProperty("payment_provider")
        private String paymentProvider;

        @JsonProperty("product_cart")
        private List<ProductCart> productCart;

        @JsonProperty("refund_status")
        private String refundStatus;

        private List<Object> refunds;

        @JsonProperty("retry_attempt")
        private Integer retryAttempt;

        @JsonProperty("settlement_amount")
        private Long settlementAmount;

        @JsonProperty("settlement_currency")
        private String settlementCurrency;

        @JsonProperty("settlement_tax")
        private Long settlementTax;

        private String status;

        @JsonProperty("subscription_id")
        private String subscriptionId;

        private Long tax;

        @JsonProperty("total_amount")
        private Long totalAmount;

        @JsonProperty("updated_at")
        private Instant updatedAt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Billing {
        private String city;
        private String country;
        private String state;
        private String street;
        private String zipcode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {

        @JsonProperty("customer_id")
        private String customerId;

        private String email;

        private Map<String, Object> metadata;

        private String name;

        @JsonProperty("phone_number")
        private String phoneNumber;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductCart {

        @JsonProperty("product_id")
        private String productId;

        private Integer quantity;
    }
}
