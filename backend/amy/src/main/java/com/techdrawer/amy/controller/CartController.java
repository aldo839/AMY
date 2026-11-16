package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.CartResponseDTO;
import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.UserRepository;
import com.techdrawer.amy.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<CartResponseDTO> findCart(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Token", required = false) UUID guestToken) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID userPublicId = currentUser.getPublicId();

        return new ResponseEntity<>(cartService.getCart(userPublicId, guestToken), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CartResponseDTO> createCart(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Token", required = false) UUID guestToken) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID userPublicId = currentUser.getPublicId();

        return new ResponseEntity<>(cartService.createCart(userPublicId, guestToken), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDTO> clearCart(@PathVariable Long id) {

        return new ResponseEntity<>(cartService.clearCart(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BigDecimal> cartTotalAmount(@PathVariable Long id) {

        return new ResponseEntity<>(cartService.cartTotalAmount(id), HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {

        cartService.deleteCart(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
