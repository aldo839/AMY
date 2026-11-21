package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.CartResponseDTO;
import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.Cart;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.CartItemRepository;
import com.techdrawer.amy.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapperService cartMapperService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private void validateOwner(UUID userPublicId, UUID guestToken) {

        if ((userPublicId == null && guestToken == null)
            || (userPublicId != null && guestToken != null)) {

            throw new IllegalArgumentException("Only one property must be provided");
        }
    }

    private Optional<CartResponseDTO> findCart(UUID userPublicId, UUID guestToken) {

        Optional<Cart> cart;

        if (userPublicId != null) {
            cart = cartRepository.findByUserPublicId(userPublicId);
        } else {
            cart = cartRepository.findByGuestToken(guestToken);
        }

        return cart.map(cartMapperService::mapToResponseDTO);
    }

    public CartResponseDTO getCart(UUID userPublicId, UUID guestToken) {

        validateOwner(userPublicId, guestToken);

        return findCart(userPublicId, guestToken)
                .orElseGet(() -> createCart(userPublicId, guestToken));
    }

    @Transactional
    public CartResponseDTO createCart(UUID userPublicId, UUID guestToken) {

        validateOwner(userPublicId, guestToken);

        return findCart(userPublicId, guestToken)
                .orElseGet(() -> {

                    Cart cart = new Cart();

                    if (userPublicId != null) {
                        cart.setUserPublicId(userPublicId);
                    } else {
                        cart.setGuestToken(guestToken);
                    }

                    Cart savedCart = cartRepository.save(cart);

                    return cartMapperService.mapToResponseDTO(savedCart);
                });
    }

    @Transactional
    public CartResponseDTO clearCart(Long id) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.clearCart();
        cartItemRepository.deleteByCartId(cart.getId());

        Cart savedCart = cartRepository.save(cart);

        return cartMapperService.mapToResponseDTO(savedCart);
    }

    public BigDecimal cartTotalAmount(Long id) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        return cart.getTotalAmount();
    }

    public void deleteCart(Long id) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cartRepository.delete(cart);
    }

    public Cart getCartEntity(UUID userPublicId, UUID guestToken) {

        validateOwner(userPublicId, guestToken);

        if (userPublicId != null) {
            return cartRepository.findByUserPublicId(userPublicId)
                    .orElseGet(() -> {
                        Cart cart = new Cart();
                        cart.setUserPublicId(userPublicId);
                        return cartRepository.save(cart);
                    });
        }

        return cartRepository.findByGuestToken(guestToken)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setGuestToken(guestToken);
                    return cartRepository.save(cart);
                });
    }

}
