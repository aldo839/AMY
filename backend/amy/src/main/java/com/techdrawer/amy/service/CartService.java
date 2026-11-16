package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.CartResponseDTO;
import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.Cart;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.CartItemRepository;
import com.techdrawer.amy.repository.CartRepository;
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
    private final AuthService authService;

    private void validateOwner(UUID userPublicId, UUID guestToken) {

        if ((userPublicId == null && guestToken == null)
            || (userPublicId != null && guestToken != null)) {

            throw new IllegalArgumentException("Only one properties must be provided");
        }
    }

    private Optional<CartResponseDTO> findCart(
            UUID userPublicId,
            UUID guestToken
    ) {

        if (userPublicId != null) {
            Cart cart = cartRepository.findByUserPublicId(userPublicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

            return Optional.ofNullable(cartMapperService.mapToResponseDTO(cart));
        }

        Cart cart = cartRepository.findByGuestToken(guestToken)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        return Optional.ofNullable(cartMapperService.mapToResponseDTO(cart));
    }

    public CartResponseDTO getCart(UUID userPublicId, UUID guestToken) {

        validateOwner(userPublicId, guestToken);

        return findCart(userPublicId, guestToken)
                .orElseGet(() -> createCart(userPublicId, guestToken));
    }

    public CartResponseDTO createCart(UUID userPublicId, UUID guestToken) {

        validateOwner(userPublicId, guestToken);

        User currentUser = authService.getCurrentUser();

        return findCart(userPublicId, guestToken)
                .orElseGet(() -> {

                    Cart cart = new Cart();

                    if (userPublicId != null && userPublicId.equals(currentUser.getPublicId())) {

                        cart.setUserPublicId(userPublicId);

                    } else {
                        cart.setGuestToken(guestToken);
                    }

                    Cart savedCart = cartRepository.save(cart);

                    return cartMapperService.mapToResponseDTO(savedCart);
                });
    }

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
