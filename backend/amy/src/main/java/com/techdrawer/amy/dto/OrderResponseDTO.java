package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.OrderItem;
import com.techdrawer.amy.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record OrderResponseDTO(

        UUID orderNumber,
        BigDecimal shippingCost,
        String shippingAddress,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        OrderStatus status,
        Set<OrderItem> orderItems

) {
}
