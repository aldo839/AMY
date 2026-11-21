package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.ShipmentResponseDTO;
import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.*;
import com.techdrawer.amy.repository.OrderRepository;
import com.techdrawer.amy.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShippingCostCalculator shippingCostCalculator;
    private final OrderRepository orderRepository;
    private final ShipmentMapperService shipmentMapperService;
    private final ShipmentRepository shipmentRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public ShipmentResponseDTO getShipment(Long id) {

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        return shipmentMapperService.mapToResponseDTO(shipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponseDTO> getOrderShipments(UUID orderNUmber) {

        Order order = orderRepository.findByOrderNumber(orderNUmber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return shipmentRepository.findByOrder(order)
                .stream()
                .map(shipmentMapperService::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponseDTO> getMyShipments() {

        User currentUser = authService.getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Authentication required");
        }

        return shipmentRepository.findBySellerPublicId(currentUser.getPublicId())
                .stream()
                .map(shipmentMapperService::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public ShipmentResponseDTO updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Shipment not found"));

        validateStatusTransition(shipment.getStatus(), newStatus);

        shipment.setStatus(newStatus);

        return shipmentMapperService.mapToResponseDTO(shipment);
    }


    @Transactional
    public void createShipments(Order order) {

        if (order == null || order.getOrderItems() == null
                || order.getOrderItems().isEmpty()) {

            throw new IllegalArgumentException("Order items are required");
        }

        Map<UUID, List<OrderItem>> itemsBySeller =
                order.getOrderItems().stream()
                        .collect(Collectors.groupingBy(OrderItem::getSellerPublicId));

        for (Map.Entry<UUID, List<OrderItem>> entry : itemsBySeller.entrySet()) {

            UUID sellerPublicId = entry.getKey();
            List<OrderItem> items = entry.getValue();

            if (sellerPublicId == null) {
                throw new IllegalStateException(
                        "Every order item must have a seller"
                );
            }

            BigDecimal shippingCost = shippingCostCalculator.calculate(
                    sellerPublicId,
                    order.getShippingAddress(),
                    items
            );

            if (shippingCost == null || shippingCost.signum() < 0) {
                throw new IllegalStateException("Invalid shipping cost");
            }

            Shipment shipment = new Shipment();

            shipment.setOrder(order);
            shipment.setSellerPublicId(sellerPublicId);
            shipment.setShippingCost(shippingCost);
            shipment.setStatus(ShipmentStatus.PENDING);

            // 1. Enregistrer la livraison en base de données
            Shipment savedShipment = shipmentRepository.saveAndFlush(shipment);

            // 2. Ajouter la livraison à la commande
            order.addShipment(savedShipment);

            // 3. Associer les articles à la livraison persistée
            for (OrderItem item : items) {
                item.setShipment(savedShipment);
            }
        }
    }


    private void validateStatusTransition(ShipmentStatus current, ShipmentStatus next) {

        if (current == null) {
            throw new IllegalStateException("Shipment status is not initialized");
        }

        boolean valid = switch (current) {
            case PENDING -> next == ShipmentStatus.PREPARING
                    || next == ShipmentStatus.FAILED;

            case PREPARING -> next == ShipmentStatus.SHIPPED
                    || next == ShipmentStatus.FAILED;

            case SHIPPED -> next == ShipmentStatus.IN_TRANSIT
                    || next == ShipmentStatus.DELIVERED
                    || next == ShipmentStatus.FAILED;

            case IN_TRANSIT -> next == ShipmentStatus.DELIVERED
                    || next == ShipmentStatus.FAILED;

            case DELIVERED, FAILED -> false;
        };

        if (!valid) {
            throw new IllegalStateException("Invalid shipment status transition: " + current + " -> " + next);
        }
    }
}
