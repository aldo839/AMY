package com.techdrawer.amy.dto;

import java.math.BigDecimal;

public record CartItemResponseDTO(

        Long id,
        Integer quantity,
        BigDecimal unitPrice,
        String title,
        String artist,
        Long cartId

){
}
