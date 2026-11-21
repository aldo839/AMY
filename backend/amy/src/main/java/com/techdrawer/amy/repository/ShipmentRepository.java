package com.techdrawer.amy.repository;

import com.techdrawer.amy.model.Order;
import com.techdrawer.amy.model.Shipment;
import org.hibernate.internal.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByOrder(Order order);

    List<Shipment> findBySellerPublicId(UUID sellerPublicId);

    Optional<Shipment> findByIdAndSellerPublicId(Long id, UUID sellerPublicId);

}
