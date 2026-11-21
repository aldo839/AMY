package com.techdrawer.amy.model;

import com.techdrawer.amy.exception.ResourceNotFoundException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<CartItem> items = new HashSet<>();

    @Column(unique = true)
    private UUID userPublicId;

    @Column(unique = true)
    private UUID guestToken;

    public void addItem(CartItem item) {

        if (item == null || item.getArticle() == null) {
            throw new IllegalArgumentException("Invalid cart item");
        }

        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        if (containsArticle(item.getArticle().getId())) {
            throw new IllegalArgumentException(
                    "Article already in cart"
            );
        }

        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {

        if (item == null || item.getArticle() == null) {
            throw new IllegalArgumentException("Invalid cart item");
        }

        CartItem existingItem = items.stream()
                .filter(cartItem ->
                        cartItem.getArticle().getId()
                                .equals(item.getArticle().getId())
                )
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Article not found in cart"
                        )
                );

        items.remove(existingItem);
        existingItem.setCart(null);
    }

    public void clearCart() {

        items.forEach(item -> item.setCart(null));
        items.clear();
    }

    public boolean containsArticle(Long articleId) {

        if (articleId == null) {
            return false;
        }

        return items.stream()
                .anyMatch(item ->
                        item.getArticle() != null
                                && articleId.equals(item.getArticle().getId())
                );
    }

    public BigDecimal getTotalAmount() {

        return items.stream()
                .map(item -> {
                    Article article = item.getArticle();

                    BigDecimal price = article.getPrice();
                    BigDecimal discount =
                            article.getDiscountPercentage() != null
                                    ? article.getDiscountPercentage()
                                    : BigDecimal.ZERO;

                    BigDecimal finalPrice = price.subtract(price.multiply(discount)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                    );

                    return finalPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @PrePersist
    @PreUpdate
    private void validateOwner() {

        if ((userPublicId == null && guestToken == null) || (userPublicId != null && guestToken != null)) {

            throw new IllegalStateException("A cart must have exactly one owner");
        }
    }
}
