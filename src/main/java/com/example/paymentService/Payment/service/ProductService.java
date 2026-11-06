package com.example.paymentService.Payment.service;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.models.misc.Currency;
import com.dodopayments.api.models.misc.TaxCategory;
import com.dodopayments.api.models.products.Price;
import com.dodopayments.api.models.products.Product;
import com.dodopayments.api.models.products.ProductCreateParams;
import com.example.paymentService.Models.PaymentItems;
import com.example.paymentService.Payment.DTO.ProductDTO;
import com.example.paymentService.Payment.repository.PaymentItemsRepository;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final DodoPaymentsClient client;
    private final PaymentItemsRepository paymentItemsRepository;

    public Pair<List<Product>, List<Integer>> createProducts(List<ProductDTO> products) {

        List<Product> productList = new ArrayList<>();
        List<Integer> paymentItemsIDs = new ArrayList<>();

        for (ProductDTO product : products) {

            Price price = Price.ofOneTime(
                    Price.OneTimePrice.builder()
                            .price(product.price())
                            .currency(Currency.INR)
                            .build()
            );

            ProductCreateParams productCreateParams =
                    ProductCreateParams.builder()
                            .name(product.name())
                            .price(price)
                            .taxCategory(TaxCategory.DIGITAL_PRODUCTS)
                            .build();

            Product createdProduct =
                    client.products().create(productCreateParams);

            // Save payment item in database
            PaymentItems paymentItems = new PaymentItems();

            paymentItems.setProductId(product.productID());
            paymentItems.setQuantity(product.quantity());
            paymentItems.setPrice(BigDecimal.valueOf(product.price()));
            paymentItems.setProductName(product.name());

            paymentItems.setTotalAmount(
                    BigDecimal.valueOf(product.price())
                            .multiply(BigDecimal.valueOf(product.quantity()))
            );

            PaymentItems p = paymentItemsRepository.save(paymentItems);

            paymentItemsIDs.add(Math.toIntExact(p.getId()));

            productList.add(createdProduct);
        }

        return new Pair<>(productList, paymentItemsIDs);
    }
}