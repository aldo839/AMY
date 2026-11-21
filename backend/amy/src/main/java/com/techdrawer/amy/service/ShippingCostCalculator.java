package com.techdrawer.amy.service;

import com.techdrawer.amy.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ShippingCostCalculator {

    BigDecimal calculate(UUID sellerPublicId, String destination, List<OrderItem> items);

}

