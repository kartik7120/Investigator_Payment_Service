package com.example.paymentService.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "payment_id",
            referencedColumnName = "id"
//            nullable = false
    )
    private Payments payment;

    private String sessionID;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Formula("price * quantity")
    private BigDecimal totalAmount;

    public BigDecimal getAmount() {

        return totalAmount;
    }
}