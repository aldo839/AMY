package com.techdrawer.amy.controller;

import com.techdrawer.amy.repository.UserRepository;
import com.techdrawer.amy.service.CartItemService;
import com.techdrawer.amy.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart-item")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
    private final CartService cartService;
    private final UserRepository userRepository;

    @PostMapping("/add/{id}")
    public ResponseEntity<Void> addItem(
            @PathVariable Long id,
            @RequestHeader(value = "X-Guest-Token", required = false) UUID guestToken,
            @RequestParam(required = false, defaultValue = "1") Integer quantity) {

        cartItemService.addItem(guestToken, id, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/remove/{id}")
    public ResponseEntity<Void> addItem(
            @PathVariable Long id,
            @RequestHeader(value = "X-Guest-Token", required = false) UUID guestToken) {

        cartItemService.removeItem(guestToken, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateItemQuantity(
            @PathVariable Long id,
            @RequestHeader(value = "X-Guest-Token", required = false) UUID guestToken,
            int quantity) {

        cartItemService.updateItemQuantity(guestToken, id, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
