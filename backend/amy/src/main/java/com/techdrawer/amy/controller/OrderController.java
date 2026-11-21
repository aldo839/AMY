package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.OrderRegistrationDTO;
import com.techdrawer.amy.dto.OrderResponseDTO;
import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.UserRepository;
import com.techdrawer.amy.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<OrderResponseDTO> findOrder(
            @PathVariable UUID orderNumber,
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) UUID guestToken) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID userPublicId = currentUser.getPublicId();

        return new ResponseEntity<>(orderService.findOrder(orderNumber, userPublicId, guestToken), HttpStatus.OK);
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<BigDecimal> orderTotalAmount(
            @PathVariable UUID orderNumber,
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) UUID guestToken) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID userPublicId = currentUser.getPublicId();

        return new ResponseEntity<>(orderService.orderTotalAmount(orderNumber, userPublicId, guestToken), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDTO> createOrder(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) UUID guestToken,
            @RequestBody @Valid OrderRegistrationDTO dto) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID userPublicId = currentUser.getPublicId();

        return new ResponseEntity<>(orderService.createOrder(userPublicId, guestToken, dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderNumber}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderNumber,
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Email", required = false) UUID guestToken) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID userPublicId = currentUser.getPublicId();

        orderService.cancelOrder(orderNumber, userPublicId, guestToken);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
