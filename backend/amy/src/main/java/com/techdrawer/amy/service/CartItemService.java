package com.techdrawer.amy.service;

import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.Article;
import com.techdrawer.amy.model.Cart;
import com.techdrawer.amy.model.CartItem;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.ArticleRepository;
import com.techdrawer.amy.repository.CartItemRepository;
import com.techdrawer.amy.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartService cartService;
    private final AuthService authService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public void addItem(UUID guestToken, Long articleId, int quantity) {

        User connectedUser = authService.getCurrentUser();

        Cart cart = cartService.getCartEntity(connectedUser != null ? connectedUser.getPublicId() : null, guestToken);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        if (quantity > article.getAvailableQuantity()) {
            throw new IllegalArgumentException("Quantity can't be more dans available");
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getArticle().getId().equals(articleId))
                .findFirst()
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setArticle(article);
            cartItem.setQuantity(quantity);

            cart.getItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Transactional
    public void removeItem(UUID guestToken, Long articleId) {

        User connectedUser = authService.getCurrentUser();
        Cart cart = cartService.getCartEntity(connectedUser != null ? connectedUser.getPublicId() : null, guestToken);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getArticle().getId().equals(articleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Article not found in cart"));

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        cartRepository.save(cart);
    }

    @Transactional
    public void updateItemQuantity(UUID guestToken, Long articleId, int quantity) {

        User connectedUser = authService.getCurrentUser();
        Cart cart = cartService.getCartEntity(connectedUser != null ? connectedUser.getPublicId() : null, guestToken);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getArticle().getId().equals(articleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Article not found in cart"));

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }
}