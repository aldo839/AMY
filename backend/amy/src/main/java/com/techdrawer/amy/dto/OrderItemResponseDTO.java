package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.Order;

public record OrderItemResponseDTO(

        Long id,
        Order order,
        String articleName,
        String seller,
        Integer quantity

) {
}
