package com.techdrawer.amy.service;

import com.techdrawer.amy.model.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ShippingCostCalculatorImpl implements ShippingCostCalculator {

    @Override
    public BigDecimal calculate(UUID sellerPublicId, String destination, List<OrderItem> items) {

        if (sellerPublicId == null) {
            throw new IllegalArgumentException("Seller is required");
        }

        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Shipping destination is required");
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Shipment must contain at least one item");
        }

        return BigDecimal.ZERO.setScale(2);
    }
}
