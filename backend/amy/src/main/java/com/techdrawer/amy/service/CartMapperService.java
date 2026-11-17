package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.CartResponseDTO;
import com.techdrawer.amy.model.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartMapperService {

    private final CartItemMapperService cartItemMapperService;

    public CartResponseDTO mapToResponseDTO(Cart cart) {

        List<List<?>> itemDTOs = Collections.singletonList(cart.getItems() != null
                ? cart.getItems().stream()
                .map(cartItemMapperService::mapToResponseDTO)
                .toList()
                : List.of());

        return new CartResponseDTO(

                cart.getId(),
                cart.getTotalAmount(),
                itemDTOs,
                cart.getUserPublicId(),
                cart.getGuestEmail()

        );
    }
}
