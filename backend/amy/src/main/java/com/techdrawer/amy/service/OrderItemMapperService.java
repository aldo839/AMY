package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.OrderItemResponseDTO;
import com.techdrawer.amy.model.OrderItem;
import org.springframework.stereotype.Service;

@Service
public class OrderItemMapperService {

    public OrderItemResponseDTO mapToResponseDTO(OrderItem orderItem) {

        return new OrderItemResponseDTO(

                orderItem.getId(),
                orderItem.getOrder(),
                orderItem.getArticle().getTitle(),
                orderItem.getArticle().getArtist().getUser().getName(),
                orderItem.getQuantity()

        );
    }
}
