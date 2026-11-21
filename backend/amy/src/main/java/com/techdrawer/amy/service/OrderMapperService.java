package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.OrderResponseDTO;
import com.techdrawer.amy.model.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderMapperService {

    public OrderResponseDTO mapToResponseDTO(Order order) {

        return new OrderResponseDTO(

                order.getOrderNumber(),
                order.getShippingCost(),
                order.getShippingAddress(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getOrderItems()

        );
    }
}
