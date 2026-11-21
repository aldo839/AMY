
package com.techdrawer.amy.repository;

import com.techdrawer.amy.model.Order;
import com.techdrawer.amy.model.ReservationStatus;
import com.techdrawer.amy.model.StockReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StockReservationRepository
        extends JpaRepository<StockReservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT sr
        FROM StockReservation sr
        WHERE sr.order = :order
          AND sr.status = :status
        ORDER BY sr.article.id ASC
    """)
    List<StockReservation> findByOrderAndStatusForUpdate(
            @Param("order") Order order,
            @Param("status") ReservationStatus status
    );

    boolean existsByOrderAndStatus(
            Order order,
            ReservationStatus status
    );

    @Query("""
        SELECT DISTINCT sr.order.id
        FROM StockReservation sr
        WHERE sr.status = :status
          AND sr.expiresAt <= :now
        ORDER BY sr.order.id ASC
    """)
    List<Long> findDistinctOrderIdsWithExpiredReservations(
            @Param("status") ReservationStatus status,
            @Param("now") LocalDateTime now
    );
}
