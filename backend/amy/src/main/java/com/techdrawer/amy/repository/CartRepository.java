package com.techdrawer.amy.repository;

import com.techdrawer.amy.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserPublicId(UUID userPublicId);

    Optional<Cart> findByGuestToken(UUID guestToken);

}
