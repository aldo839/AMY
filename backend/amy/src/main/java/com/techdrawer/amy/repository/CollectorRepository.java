package com.techdrawer.amy.repository;

import com.techdrawer.amy.model.Collector;
import com.techdrawer.amy.model.CollectorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollectorRepository extends JpaRepository<Collector, Long> {

    Optional<Collector> findByUser_PublicId(UUID publicId);

    Optional<Collector> findByCollectorType(CollectorType type);

}
