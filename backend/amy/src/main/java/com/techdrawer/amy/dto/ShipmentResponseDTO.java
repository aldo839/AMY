package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.ShipmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentResponseDTO(

        Long id,

        UUID orderNumber,

        UUID sellerPublicId,

        ShipmentStatus status,
        BigDecimal shippingCost,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
