package com.techdrawer.amy.dto;

public record CartItemResponseDTO(

        Long id,
        Integer quantity,
        String title,
        String artist,
        Long cartId

){
}
