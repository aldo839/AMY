package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.OrderRegistrationDTO;
import com.techdrawer.amy.dto.OrderResponseDTO;
import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.Cart;
import com.techdrawer.amy.model.Order;
import com.techdrawer.amy.model.OrderStatus;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.CartRepository;
import com.techdrawer.amy.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderMapperService orderMapperService;
    private final OrderItemService orderItemService;
    private final StockReservationService stockReservationService;
    private final ShipmentService shipmentService;

    private void validateOwner(UUID userPublicId, UUID guestToken) {

        if ((userPublicId == null && guestToken == null) || (userPublicId != null && guestToken != null)) {

            throw new IllegalArgumentException("Exactly one owner identifier must be provided");
        }
    }

    private User validateAuthenticatedOwner(UUID userPublicId) {

        User currentUser = authService.getCurrentUser();

        if (currentUser == null || !currentUser.getPublicId().equals(userPublicId)) {

            throw new AccessDeniedException("Access denied");
        }

        return currentUser;
    }

    private void validateOwnership(Order order, UUID userPublicId, UUID guestToken) {

        validateOwner(userPublicId, guestToken);

        if (userPublicId != null) {

            validateAuthenticatedOwner(userPublicId);

            if (!userPublicId.equals(order.getUserPublicId())) {
                throw new AccessDeniedException("Access denied");
            }

        } else if (!guestToken.equals(order.getGuestToken())) {

            throw new AccessDeniedException("Access denied");
        }
    }

    private Cart findCart(UUID userPublicId, UUID guestToken) {

        if (userPublicId != null) {

            validateAuthenticatedOwner(userPublicId);

            return cartRepository.findByUserPublicId(userPublicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        }

        return cartRepository.findByGuestToken(guestToken)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    private void validateCart(Cart cart) {

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {

            throw new IllegalArgumentException("Cart is empty");
        }
    }

    private void populateOwnerInformation(Order order, OrderRegistrationDTO dto, UUID userPublicId, User currentUser) {

        order.setShippingAddress(dto.getShippingAddress());
        order.setOwnerFirstName(dto.getOwnerFirstName());
        order.setOwnerLastName(dto.getOwnerLastName());
        order.setOwnerPhoneNumber(dto.getOwnerPhoneNumber());

        if (userPublicId != null) {

            order.setOwnerEmail(currentUser.getEmail());

        } else {

            if (dto.getOwnerEmail() == null || dto.getOwnerEmail().isBlank()) {

                throw new IllegalArgumentException("Guest email is required");
            }

            order.setOwnerEmail(dto.getOwnerEmail());
        }
    }


    @Transactional
    public OrderResponseDTO createOrder(UUID userPublicId, UUID guestToken, OrderRegistrationDTO dto) {

        validateOwner(userPublicId, guestToken);

        if (dto == null) {
            throw new IllegalArgumentException("Order information is required");
        }

        User currentUser = userPublicId != null
                ? validateAuthenticatedOwner(userPublicId)
                : null;

        Cart cart = findCart(userPublicId, guestToken);

        validateCart(cart);

        Order order = new Order();

        order.setOrderNumber(UUID.randomUUID());
        order.setUserPublicId(userPublicId);
        order.setGuestToken(guestToken);

        populateOwnerInformation(order, dto, userPublicId, currentUser);

        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        Set<com.techdrawer.amy.model.OrderItem> items =
                orderItemService.createOrderItems(cart, order);

        items.forEach(order::addOrderItem);

        Order savedOrder = orderRepository.save(order);

        stockReservationService.reserveStock(savedOrder);

        shipmentService.createShipments(savedOrder);

        savedOrder.calculateTotals();
        savedOrder = orderRepository.save(savedOrder);

        return orderMapperService.mapToResponseDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findOrder(UUID orderNumber, UUID userPublicId, UUID guestToken) {

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found")
                );

        validateOwnership(order, userPublicId, guestToken);

        return orderMapperService.mapToResponseDTO(order);
    }

    @Transactional(readOnly = true)
    public BigDecimal orderTotalAmount(UUID orderNumber, UUID userPublicId, UUID guestToken) {

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        validateOwnership(order, userPublicId, guestToken);

        return order.getTotalAmount();
    }


    @Transactional
    public void cancelOrder(UUID orderNumber, UUID userPublicId, UUID guestToken) {

        Order order = orderRepository.findByOrderNumberForUpdate(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        validateOwnership(order, userPublicId, guestToken);

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("This order cannot be cancelled");
        }

        stockReservationService.releaseReservations(order);

        order.setStatus(OrderStatus.CANCELLED);
    }

}
