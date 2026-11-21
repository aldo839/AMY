
package com.techdrawer.amy.service;

import com.techdrawer.amy.model.Article;
import com.techdrawer.amy.model.Order;
import com.techdrawer.amy.model.OrderStatus;
import com.techdrawer.amy.model.OrderItem;
import com.techdrawer.amy.model.ReservationStatus;
import com.techdrawer.amy.model.StockReservation;
import com.techdrawer.amy.repository.ArticleRepository;
import com.techdrawer.amy.repository.OrderRepository;
import com.techdrawer.amy.repository.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class StockReservationService {

    private static final long RESERVATION_MINUTES = 30;

    private final ArticleRepository articleRepository;
    private final StockReservationRepository reservationRepository;
    private final OrderRepository orderRepository;

    /**
     * Réserve le stock pour chaque article de la commande.
     */
    @Transactional
    public void reserveStock(Order order) {

        if (order == null || order.getId() == null
                || order.getOrderItems() == null
                || order.getOrderItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Order items are required"
            );
        }

        Order lockedOrder = orderRepository.findByIdForUpdate(order.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found")
                );

        if (lockedOrder.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Order is not awaiting payment"
            );
        }

        if (reservationRepository.existsByOrderAndStatus(
                lockedOrder,
                ReservationStatus.ACTIVE
        )) {
            throw new IllegalStateException(
                    "Stock is already reserved for this order"
            );
        }

        // TreeMap garantit un ordre croissant des identifiants.
        Map<Long, Integer> requestedQuantities = new TreeMap<>();

        for (OrderItem item : lockedOrder.getOrderItems()) {

            if (item.getArticle() == null
                    || item.getArticle().getId() == null
                    || item.getQuantity() == null
                    || item.getQuantity() <= 0) {

                throw new IllegalArgumentException(
                        "Invalid order item"
                );
            }

            requestedQuantities.merge(
                    item.getArticle().getId(),
                    item.getQuantity(),
                    Integer::sum
            );
        }

        LocalDateTime expiresAt =
                LocalDateTime.now().plusMinutes(RESERVATION_MINUTES);

        // Les articles sont verrouillés dans l'ordre croissant.
        for (Map.Entry<Long, Integer> entry
                : requestedQuantities.entrySet()) {

            Article article = articleRepository
                    .findByIdForUpdate(entry.getKey())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Article not found")
                    );

            Integer quantity = entry.getValue();

            validateAvailability(article, quantity);

            article.setReservedQuantity(
                    article.getReservedQuantity() + quantity
            );

            StockReservation reservation = new StockReservation();

            reservation.setArticle(article);
            reservation.setOrder(lockedOrder);
            reservation.setQuantity(quantity);
            reservation.setExpiresAt(expiresAt);
            reservation.setStatus(ReservationStatus.ACTIVE);

            reservationRepository.save(reservation);
        }
    }

    /**
     * Vérifie que le stock peut être réservé.
     */
    private void validateAvailability(
            Article article,
            Integer requestedQuantity
    ) {

        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        Integer stock = article.getAvailableQuantity();
        Integer reserved = article.getReservedQuantity();

        if (stock == null || reserved == null
                || stock < 0 || reserved < 0
                || reserved > stock) {

            throw new IllegalStateException(
                    "Invalid stock state for article " + article.getId()
            );
        }

        int available = stock - reserved;

        if (available < requestedQuantity) {
            throw new IllegalStateException(
                    "Insufficient stock for article " + article.getId()
            );
        }

        if (article.getArtworkType() != null
                && article.getArtworkType().name().equals("ORIGINAL")
                && (stock > 1 || requestedQuantity > 1)) {

            throw new IllegalStateException(
                    "An original artwork can only have one copy"
            );
        }
    }

    /**
     * Confirme les réservations après validation du paiement.
     */
    @Transactional
    public void confirmReservations(Order order) {

        Order lockedOrder = lockOrder(order);

        if (lockedOrder.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Order is not awaiting payment"
            );
        }

        List<StockReservation> reservations =
                reservationRepository.findByOrderAndStatusForUpdate(
                        lockedOrder,
                        ReservationStatus.ACTIVE
                );

        if (reservations.isEmpty()) {
            throw new IllegalStateException(
                    "No active reservation found"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        // Vérifier toutes les réservations avant de modifier le stock.
        for (StockReservation reservation : reservations) {

            if (reservation.getExpiresAt() == null
                    || !reservation.getExpiresAt().isAfter(now)) {

                throw new IllegalStateException(
                        "Reservation has expired"
                );
            }
        }

        // Les réservations sont déjà ordonnées par article.
        for (StockReservation reservation : reservations) {

            Article article = articleRepository
                    .findByIdForUpdate(
                            reservation.getArticle().getId()
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException("Article not found")
                    );

            int quantity = reservation.getQuantity();

            if (article.getAvailableQuantity() == null
                    || article.getReservedQuantity() == null
                    || article.getAvailableQuantity() < quantity
                    || article.getReservedQuantity() < quantity) {

                throw new IllegalStateException(
                        "Invalid stock quantities for article "
                                + article.getId()
                );
            }

            article.setAvailableQuantity(
                    article.getAvailableQuantity() - quantity
            );

            article.setReservedQuantity(
                    article.getReservedQuantity() - quantity
            );

            reservation.setStatus(ReservationStatus.CONFIRMED);
        }
    }

    /**
     * Libère toutes les réservations encore actives d'une commande.
     */
    @Transactional
    public void releaseReservations(Order order) {

        Order lockedOrder = lockOrder(order);

        List<StockReservation> reservations =
                reservationRepository.findByOrderAndStatusForUpdate(
                        lockedOrder,
                        ReservationStatus.ACTIVE
                );

        releaseActiveReservations(reservations, null);
    }

    /**
     * Expire les commandes dont au moins une réservation est expirée.
     *
     * Une expiration entraîne la libération de toutes les autres
     * réservations actives de la même commande.
     */
    @Transactional
    public void expireReservations() {

        LocalDateTime now = LocalDateTime.now();

        List<Long> orderIds =
                reservationRepository
                        .findDistinctOrderIdsWithExpiredReservations(
                                ReservationStatus.ACTIVE,
                                now
                        );

        for (Long orderId : orderIds) {

            Order lockedOrder = orderRepository
                    .findByIdForUpdate(orderId)
                    .orElse(null);

            if (lockedOrder == null) {
                continue;
            }

            if (lockedOrder.getStatus() != OrderStatus.PENDING_PAYMENT
                    && lockedOrder.getStatus() != OrderStatus.CANCELLED) {
                continue;
            }

            List<StockReservation> activeReservations =
                    reservationRepository.findByOrderAndStatusForUpdate(
                            lockedOrder,
                            ReservationStatus.ACTIVE
                    );

            boolean hasExpiredReservation =
                    activeReservations.stream().anyMatch(
                            reservation ->
                                    reservation.getExpiresAt() == null
                                            || !reservation.getExpiresAt().isAfter(now)
                    );

            if (!hasExpiredReservation) {
                continue;
            }

            // Expirer les réservations déjà échues et libérer
            // les autres réservations actives de cette commande.
            releaseActiveReservations(activeReservations, now);

            if (lockedOrder.getStatus() == OrderStatus.PENDING_PAYMENT) {
                lockedOrder.setStatus(OrderStatus.CANCELLED);
            }
        }
    }

    /**
     * Libère les quantités et modifie le statut des réservations.
     *
     * Si expirationTime est fourni :
     * - réservations échues : EXPIRED
     * - réservations non échues : RELEASED
     *
     * Sinon, toutes les réservations deviennent RELEASED.
     */
    private void releaseActiveReservations(
            List<StockReservation> reservations,
            LocalDateTime expirationTime
    ) {

        // Les articles sont traités dans un ordre déterministe.
        List<StockReservation> orderedReservations =
                new ArrayList<>(reservations);

        orderedReservations.sort(
                Comparator.comparing(
                        reservation -> reservation.getArticle().getId()
                )
        );

        for (StockReservation reservation : orderedReservations) {

            Article article = articleRepository
                    .findByIdForUpdate(
                            reservation.getArticle().getId()
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException("Article not found")
                    );

            int quantity = reservation.getQuantity();

            if (article.getReservedQuantity() == null
                    || article.getReservedQuantity() < quantity) {

                throw new IllegalStateException(
                        "Invalid reserved quantity for article "
                                + article.getId()
                );
            }

            article.setReservedQuantity(
                    article.getReservedQuantity() - quantity
            );

            if (expirationTime != null
                    && reservation.getExpiresAt() != null
                    && !reservation.getExpiresAt().isAfter(expirationTime)) {

                reservation.setStatus(ReservationStatus.EXPIRED);

            } else if (expirationTime != null
                    && reservation.getExpiresAt() == null) {

                reservation.setStatus(ReservationStatus.EXPIRED);

            } else {

                reservation.setStatus(ReservationStatus.RELEASED);
            }
        }
    }

    /**
     * Recharge la commande avec un verrou pessimiste.
     */
    private Order lockOrder(Order order) {

        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order is required");
        }

        return orderRepository.findByIdForUpdate(order.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found")
                );
    }
}
