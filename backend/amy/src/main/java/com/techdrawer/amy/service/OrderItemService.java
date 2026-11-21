package com.techdrawer.amy.service;

import com.techdrawer.amy.model.Article;
import com.techdrawer.amy.model.Cart;
import com.techdrawer.amy.model.CartItem;
import com.techdrawer.amy.model.Order;
import com.techdrawer.amy.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    public Set<OrderItem> createOrderItems(Cart cart, Order order) {

        if (cart == null || cart.getItems() == null
                || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        if (order == null) {
            throw new IllegalArgumentException("Order is required");
        }

        Set<OrderItem> items = new LinkedHashSet<>();

        for (CartItem cartItem : cart.getItems()) {

            OrderItem item = createOrderItem(cartItem);
            item.setOrder(order);

            items.add(item);
        }

        return items;
    }

    public OrderItem createOrderItem(CartItem cartItem) {

        validateCartItem(cartItem);

        Article article = cartItem.getArticle();

        BigDecimal originalPrice = article.getPrice();

        BigDecimal discount = article.getDiscountPercentage();

        if (discount == null) {
            discount = BigDecimal.ZERO;
        }

        validateDiscount(discount);

        BigDecimal finalPrice =
                calculateFinalPrice(originalPrice, discount);

        OrderItem item = new OrderItem();

        item.setArticle(article);
        item.setTitleSnapshot(article.getTitle());
        item.setQuantity(cartItem.getQuantity());

        item.setOriginalUnitPrice(originalPrice);
        item.setDiscountPercentage(discount);
        item.setFinalUnitPrice(finalPrice);

        item.setTotalPrice(
                calculateTotalPrice(finalPrice, cartItem.getQuantity())
        );

        item.setSellerPublicId(
                article.getArtist().getUser().getPublicId()
        );

        return item;
    }

    private void validateCartItem(CartItem cartItem) {

        if (cartItem == null || cartItem.getArticle() == null) {
            throw new IllegalArgumentException("Invalid cart item");
        }

        if (cartItem.getQuantity() == null
                || cartItem.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (cartItem.getArticle().getPrice() == null
                || cartItem.getArticle().getPrice().signum() <= 0) {
            throw new IllegalArgumentException("Invalid article price");
        }
    }

    private void validateDiscount(BigDecimal discount) {

        if (discount.compareTo(BigDecimal.ZERO) < 0
                || discount.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(
                    "Discount must be between 0 and 100"
            );
        }
    }

    public BigDecimal calculateFinalPrice(
            BigDecimal originalPrice,
            BigDecimal discountPercentage
    ) {

        BigDecimal discountAmount = originalPrice
                .multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return originalPrice.subtract(discountAmount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalPrice(
            BigDecimal finalUnitPrice,
            Integer quantity
    ) {

        return finalUnitPrice
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
