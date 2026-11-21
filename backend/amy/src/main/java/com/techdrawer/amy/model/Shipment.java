package com.techdrawer.amy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private UUID sellerPublicId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingCost;

    private String trackingNumber;

    private String carrier;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;
}
