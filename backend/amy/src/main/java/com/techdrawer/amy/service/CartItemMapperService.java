package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.CartItemResponseDTO;
import com.techdrawer.amy.model.CartItem;
import org.springframework.stereotype.Service;

@Service
public class CartItemMapperService {

    public CartItemResponseDTO mapToResponseDTO(CartItem cartItem) {

        return new CartItemResponseDTO(

                cartItem.getId(),
                cartItem.getQuantity(),
                cartItem.getUnitPrice(),
                cartItem.getArticle().getTitle(),
                cartItem.getArticle().getArtist().getUser().getName(),
                cartItem.getCart().getId()

        );
    }
}
