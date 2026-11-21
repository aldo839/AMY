package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.ShipmentResponseDTO;
import com.techdrawer.amy.model.Shipment;
import org.springframework.stereotype.Service;

@Service
public class ShipmentMapperService {

    public ShipmentResponseDTO mapToResponseDTO(Shipment shipment) {

        return new ShipmentResponseDTO(

                shipment.getId(),
                shipment.getOrder().getOrderNumber(),
                shipment.getSellerPublicId(),
                shipment.getStatus(),
                shipment.getShippingCost(),
                shipment.getShippedAt(),
                shipment.getDeliveredAt()

        );
    }
}