package com.techdrawer.amy.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartResponseDTO(

        Long id,

        BigDecimal totalAmount,

        java.util.List<java.util.List<?>> items,

        UUID userPublicId,

        String guestEmail

) {
}
