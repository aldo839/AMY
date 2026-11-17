package com.techdrawer.amy.model;

import com.techdrawer.amy.exception.ResourceNotFoundException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> items;

    private UUID userPublicId;

    @Email(message = "Email must be valid")
    private String guestEmail;

    public void addItem(CartItem item) {

        if (containsArticle(item.getArticle().getId())) {
            throw new IllegalArgumentException("Article already in cart");
        }
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {

        if (!containsArticle(item.getArticle().getId())) {
            throw new ResourceNotFoundException("Article not found in cart");
        }
        items.remove(item);
        item.setCart(this);
    }

    public void clearCart() {
        this.items.clear();
    }

    public Boolean containsArticle(Long articleId) {

        return items.stream()
                .anyMatch(item -> item.getArticle().getId().equals(articleId));
    }

    public BigDecimal getTotalAmount() {

        this.totalAmount = items.stream()
                .map(CartItem::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalAmount;
    }

}
